package de.morrien.voodoo.util;

import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.block.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static de.morrien.voodoo.VoodooConfig.COMMON;

/**
 * Keeps the chunks of poppet shelves loaded while their owner is online, without ticking the chunks.
 * The positions and owners of all shelves are stored per level, so their chunks can be loaded when the owner joins.
 * Every shelf has its own ticket, so a chunk with shelves of several players stays loaded while any of them are online.
 */
public class PoppetShelfChunkLoader {
    private static final TicketType<BlockPos> TICKET = TicketType.create(Voodoo.MOD_ID + ":poppet_shelf", Vec3i::compareTo);
    private static final SavedData.Factory<ShelfChunks> FACTORY = new SavedData.Factory<>(ShelfChunks::new, ShelfChunks::load, null);

    /**
     * Registers the owner of a poppet shelf, or removes the shelf if the owner is null.
     *
     * @param level The level of the shelf
     * @param pos   The position of the shelf
     * @param owner The UUID of the owner or null
     */
    public static void setShelfOwner(ServerLevel level, BlockPos pos, @Nullable UUID owner) {
        final ShelfChunks shelfChunks = getShelfChunks(level);
        final UUID previous = owner == null ? shelfChunks.owners.remove(pos) : shelfChunks.owners.put(pos.immutable(), owner);
        if (Objects.equals(previous, owner)) return;
        shelfChunks.setDirty();
        if (previous != null && isOnline(level, previous))
            removeTicket(level, pos);
        if (owner != null && COMMON.poppetShelf.chunkLoading.get() && isOnline(level, owner))
            addTicket(level, pos);
    }

    /**
     * Loads the chunks of all shelves of a player that joined, in all levels.
     *
     * @param player The player that joined
     */
    public static void onPlayerJoin(ServerPlayer player) {
        if (!COMMON.poppetShelf.chunkLoading.get()) return;
        for (ServerLevel level : player.server.getAllLevels())
            getShelfChunks(level).owners.forEach((pos, owner) -> {
                if (owner.equals(player.getUUID()))
                    addTicket(level, pos);
            });
    }

    /**
     * Releases the chunks of all shelves of a player that left, in all levels.
     *
     * @param player The player that left
     */
    public static void onPlayerLeave(ServerPlayer player) {
        for (ServerLevel level : player.server.getAllLevels())
            getShelfChunks(level).owners.forEach((pos, owner) -> {
                if (owner.equals(player.getUUID()))
                    removeTicket(level, pos);
            });
    }

    /**
     * Loads the chunks of shelves whose owners are already online, e.g. for levels that are loaded later.
     *
     * @param level The level that was loaded
     */
    public static void onLevelLoad(ServerLevel level) {
        if (!COMMON.poppetShelf.chunkLoading.get()) return;
        getShelfChunks(level).owners.forEach((pos, owner) -> {
            if (isOnline(level, owner))
                addTicket(level, pos);
        });
    }

    /**
     * Forgets the no longer present shelves in a just loaded chunk.
     * Serves as a fallback in case the removal was not caught otherwise.
     *
     * @param level The level of the chunk
     * @param chunk The chunk that was loaded
     */
    public static void onChunkLoad(ServerLevel level, LevelChunk chunk) {
        final ShelfChunks shelfChunks = getShelfChunks(level);
        shelfChunks.owners.keySet().removeIf(pos -> {
            if (!chunk.getPos().equals(new ChunkPos(pos)) || chunk.getBlockState(pos).is(BlockRegistry.poppetShelf.get()))
                return false;
            removeTicket(level, pos);
            shelfChunks.setDirty();
            return true;
        });
    }

    /**
     * Adds or removes the tickets of all shelves whose owners are online, after the config was reloaded.
     *
     * @param server The server whose config was reloaded
     */
    public static void onConfigReload(MinecraftServer server) {
        final boolean chunkLoading = COMMON.poppetShelf.chunkLoading.get();
        for (ServerLevel level : server.getAllLevels())
            getShelfChunks(level).owners.forEach((pos, owner) -> {
                if (!isOnline(level, owner)) return;
                if (chunkLoading)
                    addTicket(level, pos);
                else
                    removeTicket(level, pos);
            });
    }

    private static boolean isOnline(ServerLevel level, UUID uuid) {
        return level.getServer().getPlayerList().getPlayer(uuid) != null;
    }

    private static void addTicket(ServerLevel level, BlockPos pos) {
        level.getChunkSource().addRegionTicket(TICKET, new ChunkPos(pos), 0, pos);
    }

    private static void removeTicket(ServerLevel level, BlockPos pos) {
        level.getChunkSource().removeRegionTicket(TICKET, new ChunkPos(pos), 0, pos);
    }

    private static ShelfChunks getShelfChunks(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, Voodoo.MOD_ID + "_poppet_shelves");
    }

    private static class ShelfChunks extends SavedData {
        private final Map<BlockPos, UUID> owners = new HashMap<>();

        private static ShelfChunks load(CompoundTag tag, HolderLookup.Provider registries) {
            final ShelfChunks shelfChunks = new ShelfChunks();
            final ListTag shelves = tag.getList("shelves", Tag.TAG_COMPOUND);
            for (int i = 0; i < shelves.size(); i++) {
                final CompoundTag shelf = shelves.getCompound(i);
                if (shelf.hasUUID("owner"))
                    shelfChunks.owners.put(BlockPos.of(shelf.getLong("pos")), shelf.getUUID("owner"));
            }
            return shelfChunks;
        }

        @Override
        public @NotNull CompoundTag save(CompoundTag tag, HolderLookup.@NotNull Provider registries) {
            final ListTag shelves = new ListTag();
            owners.forEach((pos, owner) -> {
                final CompoundTag shelf = new CompoundTag();
                shelf.putLong("pos", pos.asLong());
                shelf.putUUID("owner", owner);
                shelves.add(shelf);
            });
            tag.put("shelves", shelves);
            return tag;
        }
    }
}
