package de.morrien.voodoo.util;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.blockentity.PoppetShelfBlockEntity;
import de.morrien.voodoo.item.PoppetItem;
import de.morrien.voodoo.sound.SoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PoppetUtil {
    private static final Map<UUID, List<WeakReference<PoppetShelfBlockEntity>>> poppetShelvesCache;
    private static final WeakHashMap<PoppetShelfBlockEntity, List<Poppet>> poppetCache;

    static {
        poppetShelvesCache = new HashMap<>();
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
        if (source instanceof Player) {
            final Player fromPlayer = (Player) source;
            fromPlayer.displayClientMessage(Component.translatable("text.voodoo.voodoo_protection.had", BindingUtil.getBoundName(voodooPoppet)), true);
            voodooPoppet.hurtAndBreak(Integer.MAX_VALUE, fromPlayer, playerEntity -> {
                playerEntity.broadcastBreakEvent(playerEntity.getUsedItemHand());
                playerEntity.level.playSound(null, playerEntity, SoundRegistry.voodooProtectionPoppetUsed, SoundSource.PLAYERS, 1, 1);
            });
        } else {
            source.level.playSound(null, source, SoundRegistry.voodooProtectionPoppetUsed, SoundSource.PLAYERS, 1, 1);
            voodooPoppet.shrink(1);
        }
    }

    /**
     * Retrieve all poppets in the inventory of a player.
     *
     * @param player The player
     * @return The found poppets
     */
    public static List<Poppet> getPoppetsInInventory(Player player) {
        List<ItemStack> playerItems = new ArrayList<>();
        playerItems.addAll(player.getInventory().offhand);
        playerItems.addAll(player.getInventory().items);
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
    public static List<Poppet> getPoppetsInShelves(ServerPlayer player) {
        List<WeakReference<PoppetShelfBlockEntity>> cachedShelves = poppetShelvesCache.get(player.getUUID());
        if (cachedShelves == null) {
            cachedShelves = StreamSupport
                    .stream(player.server.getAllLevels().spliterator(), false)
                    .flatMap(world -> getPoppetShelvesStream(player.server))
                    .filter(poppetShelf -> player.getUUID().equals(poppetShelf.getOwnerUuid()))
                    .map(WeakReference::new)
                    .collect(Collectors.toList());
            poppetShelvesCache.put(player.getUUID(), cachedShelves);
        }
        final List<Poppet> poppets = new ArrayList<>();
        for (Iterator<WeakReference<PoppetShelfBlockEntity>> iterator = cachedShelves.iterator(); iterator.hasNext(); ) {
            WeakReference<PoppetShelfBlockEntity> cachedShelf = iterator.next();
            final PoppetShelfBlockEntity poppetShelf = cachedShelf.get();
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
    public static void invalidateShelfCache(PoppetShelfBlockEntity poppetShelf) {
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
            poppetShelvesCache.remove(playerUUD);
    }

    public static void removePoppetShelf(UUID ownerUUID, PoppetShelfBlockEntity poppetShelf) {
        if (ownerUUID == null || poppetShelf.getLevel() == null || poppetShelf.getLevel().isClientSide) return;
        final List<WeakReference<PoppetShelfBlockEntity>> weakShelves = poppetShelvesCache.get(ownerUUID);
        if (weakShelves != null) {
            weakShelves.removeIf(weakShelf -> weakShelf.get() == null || weakShelf.get() == poppetShelf);
            if (weakShelves.isEmpty())
                poppetShelvesCache.remove(ownerUUID);
        }
    }

    public static void addPoppetShelf(UUID ownerUUID, PoppetShelfBlockEntity poppetShelf) {
        if (ownerUUID == null || (poppetShelf.getLevel() != null && poppetShelf.getLevel().isClientSide)) return;
        removePoppetShelf(ownerUUID, poppetShelf);
        poppetShelvesCache.putIfAbsent(ownerUUID, new ArrayList<>());
        final List<WeakReference<PoppetShelfBlockEntity>> weakShelves = poppetShelvesCache.get(ownerUUID);
        weakShelves.add(new WeakReference<>(poppetShelf));
    }

    /**
     * Searches for a specific poppet that is bound to a player.
     *
     * @param player     The player that the poppet must be bound to
     * @param poppetType The type of the poppet
     * @return The found poppet or null
     */
    public static Poppet getPlayerPoppet(ServerPlayer player, Poppet.PoppetType poppetType) {
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

    public static Stream<PoppetShelfBlockEntity> getPoppetShelvesStream(MinecraftServer server) {
        return poppetShelvesCache.values().stream()
                .flatMap(Collection::stream)
                .map(WeakReference::get)
                .filter(Objects::nonNull);
        //return StreamSupport.stream(server.getAllLevels().spliterator(), false)
        //        .map(level -> level.getChunkAt(null))
        //        .flatMap(chunk -> chunk.getBlockEntities().values().stream())
        //        .filter(blockEntity -> blockEntity instanceof PoppetShelfBlockEntity)
        //        .map(blockEntity -> ((PoppetShelfBlockEntity) blockEntity))
        //        .collect(Collectors.toList());
    }
}
