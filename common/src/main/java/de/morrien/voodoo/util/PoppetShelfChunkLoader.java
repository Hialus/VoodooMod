package de.morrien.voodoo.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.block.BlockRegistry;
import de.morrien.voodoo.platform.RegistrationProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.SavedDataStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import static de.morrien.voodoo.VoodooConfig.COMMON;

/**
 * Keeps the chunks of poppet shelves loaded while their owner is online, without ticking the chunks.
 * The positions and owners of all shelves are stored per level, so their chunks can be loaded when the owner joins.
 * A chunk has one ticket while any shelves with owner are in it, so a chunk with shelves of several players stays loaded
 * while any of them are online.
 */
public class PoppetShelfChunkLoader {
    public static final RegistrationProvider<TicketType> TICKET_TYPES = RegistrationProvider.get(Registries.TICKET_TYPE);
    private static final Supplier<TicketType> TICKET = TICKET_TYPES.register("poppet_shelf", key -> new TicketType(TicketType.NO_TIMEOUT, TicketType.FLAG_LOADING));
    private static final SavedDataType<ShelfChunks> TYPE = new SavedDataType<>(Voodoo.id("poppet_shelves"), ShelfChunks::new, ShelfChunks.CODEC, DataFixTypes.SAVED_DATA_COMMAND_STORAGE);

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
        for (ServerLevel level : player.level().getServer().getAllLevels())
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
        for (ServerLevel level : player.level().getServer().getAllLevels())
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
            if (!chunk.getPos().equals(ChunkPos.containing(pos)) || chunk.getBlockState(pos).is(BlockRegistry.poppetShelf.get()))
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

    /**
     * Minecraft only retains one ticket of a type per chunk, so only the first shelf adds a ticket, which is removed only when all shelves are broken.
     */
    private static void addTicket(ServerLevel level, BlockPos pos) {
        final ChunkPos chunkPos = ChunkPos.containing(pos);
        final Set<BlockPos> holders = getShelfChunks(level).ticketHolders.computeIfAbsent(chunkPos, key -> new HashSet<>());
        if (holders.add(pos.immutable()) && holders.size() == 1)
            level.getChunkSource().addTicketWithRadius(TICKET.get(), chunkPos, 0);
    }

    private static void removeTicket(ServerLevel level, BlockPos pos) {
        final ChunkPos chunkPos = ChunkPos.containing(pos);
        final Map<ChunkPos, Set<BlockPos>> ticketHolders = getShelfChunks(level).ticketHolders;
        final Set<BlockPos> holders = ticketHolders.get(chunkPos);
        if (holders == null || !holders.remove(pos)) return;
        if (holders.isEmpty()) {
            ticketHolders.remove(chunkPos);
            level.getChunkSource().removeTicketWithRadius(TICKET.get(), chunkPos, 0);
        }
    }

    private static ShelfChunks getShelfChunks(ServerLevel level) {
        final SavedDataStorage storage = level.getDataStorage();
        ShelfChunks shelfChunks = storage.get(TYPE);
        if (shelfChunks == null) {
            shelfChunks = loadLegacyShelfChunks(level);
            storage.set(TYPE, shelfChunks);
        }
        return shelfChunks;
    }

    /**
     * Upgrades old stored shelf data to a new version.
     */
    private static ShelfChunks loadLegacyShelfChunks(ServerLevel level) {
        final Path root = level.getServer().getWorldPath(LevelResource.ROOT);
        final ResourceKey<Level> dimension = level.dimension();
        final Path folder;
        if (dimension == Level.OVERWORLD) folder = root;
        else if (dimension == Level.NETHER) folder = root.resolve("DIM-1");
        else if (dimension == Level.END) folder = root.resolve("DIM1");
        else folder = DimensionType.getStorageFolder(dimension, root);
        final Path file = folder.resolve("data").resolve(Voodoo.MOD_ID + "_poppet_shelves.dat");
        if (Files.exists(file)) {
            try {
                final CompoundTag tag = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
                final Optional<ShelfChunks> shelfChunks = ShelfChunks.CODEC.parse(NbtOps.INSTANCE, tag.getCompoundOrEmpty("data")).resultOrPartial(Voodoo.logger::error);
                if (shelfChunks.isPresent()) return shelfChunks.get();
            } catch (IOException e) {
                Voodoo.logger.error("Failed to read the poppet shelves from {}", file, e);
            }
        }
        return new ShelfChunks();
    }

    private record Shelf(BlockPos pos, UUID owner) {
        private static final Codec<Shelf> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.LONG.xmap(BlockPos::of, BlockPos::asLong).fieldOf("pos").forGetter(Shelf::pos),
                UUIDUtil.CODEC.fieldOf("owner").forGetter(Shelf::owner)
        ).apply(instance, Shelf::new));
    }

    private static class ShelfChunks extends SavedData {
        private static final Codec<ShelfChunks> CODEC = Shelf.CODEC.listOf().fieldOf("shelves").codec().xmap(ShelfChunks::new, ShelfChunks::getShelves);
        private final Map<BlockPos, UUID> owners = new HashMap<>();
        private final Map<ChunkPos, Set<BlockPos>> ticketHolders = new HashMap<>();

        private ShelfChunks() {
        }

        private ShelfChunks(List<Shelf> shelves) {
            shelves.forEach(shelf -> owners.put(shelf.pos(), shelf.owner()));
        }

        private List<Shelf> getShelves() {
            return owners.entrySet().stream().map(entry -> new Shelf(entry.getKey(), entry.getValue())).toList();
        }
    }
}
