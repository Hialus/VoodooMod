package de.morrien.voodoo.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.item.PoppetItem;
import de.morrien.voodoo.tileentity.PoppetShelfTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PoppetUtil {
    private static final Cache<UUID, List<WeakReference<PoppetShelfTileEntity>>> poppetShelvesCache;
    private static final WeakHashMap<PoppetShelfTileEntity, List<Poppet>> poppetCache;

    static {
        poppetShelvesCache = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build();
        poppetCache = new WeakHashMap<>();
    }

    public static void useVoodooProtectionPuppet(ItemStack voodooPoppet, Entity source) {
        if (source instanceof PlayerEntity) {
            final PlayerEntity fromPlayer = (PlayerEntity) source;
            fromPlayer.displayClientMessage(new TranslationTextComponent("text.voodoo.voodoo_protection.had", BindingUtil.getBoundName(voodooPoppet)), true);
            voodooPoppet.hurtAndBreak(Integer.MAX_VALUE, fromPlayer, playerEntity -> {
                playerEntity.broadcastBreakEvent(playerEntity.getUsedItemHand());
                playerEntity.level.playSound(null, playerEntity, SoundEvents.TOTEM_USE, SoundCategory.PLAYERS, 1, 1);
            });
        } else {
            source.level.playSound(null, source, SoundEvents.TOTEM_USE, SoundCategory.PLAYERS, 1, 1);
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
    public static List<Poppet> getPoppetsInShelves(PlayerEntity player) {
        List<WeakReference<PoppetShelfTileEntity>> cachedShelves = poppetShelvesCache.getIfPresent(player.getUUID());
        if (cachedShelves == null) {
            final World world = player.level;
            cachedShelves = world.blockEntityList.stream()
                    .filter(tileEntity -> tileEntity instanceof PoppetShelfTileEntity)
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
                        .map(stack -> new Poppet(player, (PoppetItem) stack.getItem(), stack))
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
        poppetCache.remove(poppetShelf);
    }

    /**
     * Clear the cache holding all PoppetShelfTileEntities.
     * Should be used everytime a new poppet shelf is created.
     * TODO: Maybe replace with method to add poppet shelf into cache instead.
     */
    public static void invalidateShelvesCache() {
        poppetShelvesCache.invalidateAll();
    }
}
