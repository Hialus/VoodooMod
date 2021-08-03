package de.morrien.voodoo.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.item.PoppetItem;
import de.morrien.voodoo.sound.SoundRegistry;
import de.morrien.voodoo.tileentity.PoppetShelfTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PoppetUtil {
    private static final Cache<UUID, List<WeakReference<PoppetShelfTileEntity>>> poppetShelvesCache;
    private static final WeakHashMap<PoppetShelfTileEntity, List<Poppet>> poppetCache;

    static {
        poppetShelvesCache = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build();
        poppetCache = new WeakHashMap<>();
    }

    /**
     * Code for handling all the events that happen when a voodoo protection poppet is activated.
     * If the voodoo poppet was used by a player they will get a message telling them that the target had a protection poppet.
     * If the voodoo poppet was used by the item entity it will not send such a message.
     * In both cases the voodoo poppet that was used will be destroyed.
     *
     * @param voodooPoppet The voodoo poppet that was used
     * @param source       The entity that used the voodoo poppet
     */
    public static void useVoodooProtectionPuppet(ItemStack voodooPoppet, Entity source) {
        if (source instanceof PlayerEntity) {
            final PlayerEntity fromPlayer = (PlayerEntity) source;
            fromPlayer.displayClientMessage(new TranslationTextComponent("text.voodoo.voodoo_protection.had", BindingUtil.getBoundName(voodooPoppet)), true);
            voodooPoppet.hurtAndBreak(Integer.MAX_VALUE, fromPlayer, playerEntity -> {
                playerEntity.broadcastBreakEvent(playerEntity.getUsedItemHand());
                playerEntity.level.playSound(null, playerEntity, SoundRegistry.voodooProtectionPoppetUsed.get(), SoundCategory.PLAYERS, 1, 1);
            });
        } else {
            source.level.playSound(null, source, SoundRegistry.voodooProtectionPoppetUsed.get(), SoundCategory.PLAYERS, 1, 1);
            voodooPoppet.shrink(1);
        }
    }

    /**
     * Retrieve all poppets in the inventory of a player.
     *
     * @param player The player
     * @return The found poppets
     */
    public static List<Poppet> getPoppetsInInventory(PlayerEntity player) {
        List<ItemStack> playerItems = new ArrayList<>();
        playerItems.addAll(player.inventory.offhand);
        playerItems.addAll(player.inventory.items);
        return playerItems.stream()
                .filter(stack -> stack.getItem() instanceof PoppetItem)
                .filter(stack -> player.getUUID().equals(BindingUtil.getBoundUUID(stack)))
                .map(stack -> new Poppet(player, (PoppetItem) stack.getItem(), stack))
                .collect(Collectors.toList());
    }

    /**
     * Retrieve all poppets of a player that are in his poppet shelves.
     * This method utilises a cache, so that it does not have to iterate through all loaded TileEntities on each call.
     *
     * @param player The player
     * @return The found poppets
     */
    public static List<Poppet> getPoppetsInShelves(ServerPlayerEntity player) {
        List<WeakReference<PoppetShelfTileEntity>> cachedShelves = poppetShelvesCache.getIfPresent(player.getUUID());
        if (cachedShelves == null) {
            cachedShelves = StreamSupport
                    .stream(player.server.getAllLevels().spliterator(), false)
                    .flatMap(world -> world.blockEntityList.stream())
                    .filter(tileEntity -> tileEntity instanceof PoppetShelfTileEntity)
                    .filter(poppetShelf -> player.getUUID().equals(((PoppetShelfTileEntity) poppetShelf).getOwnerUuid()))
                    .map(tileEntity -> new WeakReference<>((PoppetShelfTileEntity) tileEntity))
                    .collect(Collectors.toList());
            poppetShelvesCache.put(player.getUUID(), cachedShelves);
        }
        final List<Poppet> poppets = new ArrayList<>();
        for (Iterator<WeakReference<PoppetShelfTileEntity>> iterator = cachedShelves.iterator(); iterator.hasNext(); ) {
            WeakReference<PoppetShelfTileEntity> cachedShelf = iterator.next();
            final PoppetShelfTileEntity poppetShelf = cachedShelf.get();
            if (poppetShelf == null) {
                iterator.remove();
                continue;
            }
            List<Poppet> poppetList = poppetCache.get(poppetShelf);
            if (poppetList == null) {
                poppetList = poppetShelf
                        .getInventory()
                        .stream()
                        .filter(stack -> player.getUUID().equals(BindingUtil.getBoundUUID(stack)))
                        .map(stack -> new Poppet(poppetShelf, player, (PoppetItem) stack.getItem(), stack))
                        .collect(Collectors.toList());
                poppetCache.put(poppetShelf, poppetList);
            }
            poppets.addAll(poppetList);
        }
        return poppets;
    }

    /**
     * Clear the cached poppets of a poppet shelf.
     * Should be used everytime the inventory of a poppet shelf changes
     *
     * @param poppetShelf The poppet shelf
     */
    public static void invalidateShelfCache(PoppetShelfTileEntity poppetShelf) {
        if (poppetShelf != null)
            poppetCache.remove(poppetShelf);
    }

    /**
     * Clear the cached poppets shelves of a player.
     * Should be used everytime a new poppet shelf of a player is created.
     *
     * @param playerUUD The UUID of the owner of the player
     */
    public static void invalidateShelvesCache(UUID playerUUD) {
        if (playerUUD != null)
            poppetShelvesCache.invalidate(playerUUD);
    }

    /**
     * Searches for a specific poppet that is bound to a player.
     *
     * @param player     The player that the poppet must be bound to
     * @param poppetType The type of the poppet
     * @return The found poppet or null
     */
    public static Poppet getPlayerPoppet(ServerPlayerEntity player, Poppet.PoppetType poppetType) {
        return getPoppetsInInventory(player)
                .stream()
                .filter(poppet -> poppet.getItem().getPoppetType() == poppetType)
                .findFirst()
                .orElseGet(() -> getPoppetsInShelves(player)
                        .stream()
                        .filter(poppet -> poppet.getItem().getPoppetType() == poppetType)
                        .findFirst()
                        .orElse(null));

    }
}
