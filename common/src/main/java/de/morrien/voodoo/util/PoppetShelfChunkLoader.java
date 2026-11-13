package de.morrien.voodoo.util;

import de.morrien.voodoo.Voodoo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

import static de.morrien.voodoo.VoodooConfig.COMMON;

/**
 * Keeps the chunks of poppet shelves loaded, without ticking them.
 * The positions of all shelves are stored per level, so they can be loaded when the level is loaded
 */
public class PoppetShelfChunkLoader {
    private static final TicketType<BlockPos> TICKET = TicketType.create(Voodoo.MOD_ID + ":poppet_shelf", Vec3i::compareTo);
    private static final SavedData.Factory<ShelfChunks> FACTORY = new SavedData.Factory<>(ShelfChunks::new, ShelfChunks::load, null);

    public static void setShelfLoaded(ServerLevel level, BlockPos pos, boolean loaded) {
        final ShelfChunks shelfChunks = getShelfChunks(level);
        if (loaded ? shelfChunks.positions.add(pos) : shelfChunks.positions.remove(pos)) {
            shelfChunks.setDirty();
            if (loaded)
                level.getChunkSource().addRegionTicket(TICKET, new ChunkPos(pos), 0, pos);
            else
                level.getChunkSource().removeRegionTicket(TICKET, new ChunkPos(pos), 0, pos);
        }
    }

    /**
     * Ensure that tickets are added when a level is loaded.
     *
     * @param level The level that was loaded
     */
    public static void onLevelLoad(ServerLevel level) {
        final ShelfChunks shelfChunks = getShelfChunks(level);
        if (COMMON.poppetShelf.chunkLoading.get()) {
            for (BlockPos pos : shelfChunks.positions)
                level.getChunkSource().addRegionTicket(TICKET, new ChunkPos(pos), 0, pos);
        } else if (!shelfChunks.positions.isEmpty()) {
            shelfChunks.positions.clear();
            shelfChunks.setDirty();
        }
    }

    private static ShelfChunks getShelfChunks(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, Voodoo.MOD_ID + "_poppet_shelves");
    }

    private static class ShelfChunks extends SavedData {
        private final Set<BlockPos> positions = new HashSet<>();

        private static ShelfChunks load(CompoundTag tag, HolderLookup.Provider registries) {
            final ShelfChunks shelfChunks = new ShelfChunks();
            for (long pos : tag.getLongArray("positions"))
                shelfChunks.positions.add(BlockPos.of(pos));
            return shelfChunks;
        }

        @Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
            tag.putLongArray("positions", positions.stream().mapToLong(BlockPos::asLong).toArray());
            return tag;
        }
    }
}
