package de.morrien.voodoo.gametest;

import de.morrien.voodoo.block.BlockRegistry;
import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.util.PoppetShelfChunkLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

import static de.morrien.voodoo.VoodooConfig.COMMON;
import static de.morrien.voodoo.gametest.TestUtil.*;

/**
 * Test with shelves far away from the test area, so they must load themselves.
 * Ensures that shelves are far enough away from each other to not interfere with other tests.
 */
public class ChunkLoadingTests {
    public static List<TestFunction> tests() {
        return List.of(
                test("chunk_loading.loads_while_owner_online", ChunkLoadingTests::loadsWhileOwnerOnline),
                test("chunk_loading.loads_when_owner_joins", ChunkLoadingTests::loadsWhenOwnerJoins),
                test("chunk_loading.shared_chunk", ChunkLoadingTests::sharedChunk),
                test("chunk_loading.placed_and_broken_shelf", ChunkLoadingTests::placedAndBrokenShelf),
                test("chunk_loading.forgets_missing_shelf", 400, ChunkLoadingTests::forgetsMissingShelf),
                configTest("chunk_loading.config_reload", ChunkLoadingTests::configReload)
        );
    }

    private static void loadsWhileOwnerOnline(GameTestHelper helper) {
        final ServerLevel level = helper.getLevel();
        final ServerPlayer player = makePlayer(helper);
        final BlockPos pos = farShelf(helper, 2000);
        PoppetShelfChunkLoader.setShelfOwner(level, pos, player.getUUID());
        helper.startSequence()
                .thenWaitUntil(() -> assertLoaded(helper, pos, true))
                .thenExecute(() -> disconnect(player))
                .thenWaitUntil(() -> assertLoaded(helper, pos, false))
                .thenExecute(() -> PoppetShelfChunkLoader.setShelfOwner(level, pos, null))
                .thenSucceed();
    }

    private static void loadsWhenOwnerJoins(GameTestHelper helper) {
        final ServerLevel level = helper.getLevel();
        final UUID owner = UUID.randomUUID();
        final BlockPos pos = farShelf(helper, 2400);
        PoppetShelfChunkLoader.setShelfOwner(level, pos, owner);
        helper.startSequence()
                .thenIdle(5)
                .thenExecute(() -> assertLoaded(helper, pos, false))
                .thenExecute(() -> makePlayer(helper, new Vec3(1.5, 1, 1.5), owner))
                .thenWaitUntil(() -> assertLoaded(helper, pos, true))
                .thenExecute(() -> PoppetShelfChunkLoader.setShelfOwner(level, pos, null))
                .thenWaitUntil(() -> assertLoaded(helper, pos, false))
                .thenSucceed();
    }

    private static void sharedChunk(GameTestHelper helper) {
        final ServerLevel level = helper.getLevel();
        final ServerPlayer first = makePlayer(helper);
        final ServerPlayer second = makePlayer(helper);
        final BlockPos firstPos = farShelf(helper, 2800);
        final BlockPos secondPos = firstPos.east();
        level.setBlockAndUpdate(secondPos, BlockRegistry.poppetShelf.get().defaultBlockState());
        PoppetShelfChunkLoader.setShelfOwner(level, firstPos, first.getUUID());
        PoppetShelfChunkLoader.setShelfOwner(level, secondPos, second.getUUID());
        helper.startSequence()
                .thenWaitUntil(() -> assertLoaded(helper, firstPos, true))
                .thenExecute(() -> disconnect(first))
                .thenIdle(5)
                .thenExecute(() -> assertLoaded(helper, firstPos, true))
                .thenExecute(() -> disconnect(second))
                .thenWaitUntil(() -> assertLoaded(helper, firstPos, false))
                .thenExecute(() -> {
                    PoppetShelfChunkLoader.setShelfOwner(level, firstPos, null);
                    PoppetShelfChunkLoader.setShelfOwner(level, secondPos, null);
                })
                .thenSucceed();
    }

    private static void placedAndBrokenShelf(GameTestHelper helper) {
        final ServerLevel level = helper.getLevel();
        final ServerPlayer player = makePlayer(helper);
        final BlockPos pos = farPos(helper, 3200);
        level.setBlockAndUpdate(pos.below(), Blocks.STONE.defaultBlockState());
        final ItemStack stack = new ItemStack(ItemRegistry.poppetShelf.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(pos.below()), Direction.UP, pos.below(), false)));
        helper.assertTrue(level.getBlockState(pos).is(BlockRegistry.poppetShelf.get()), "Shelf should be placed");
        helper.startSequence()
                .thenIdle(5)
                .thenExecute(() -> assertLoaded(helper, pos, true))
                .thenExecute(() -> level.destroyBlock(pos, false))
                .thenWaitUntil(() -> assertLoaded(helper, pos, false))
                .thenSucceed();
    }

    private static void configReload(GameTestHelper helper) {
        final ServerLevel level = helper.getLevel();
        final ServerPlayer player = makePlayer(helper);
        final BlockPos pos = farShelf(helper, 3600);
        setConfig(COMMON.poppetShelf.chunkLoading, false);
        PoppetShelfChunkLoader.setShelfOwner(level, pos, player.getUUID());
        helper.startSequence()
                .thenIdle(5)
                .thenExecute(() -> assertLoaded(helper, pos, false))
                .thenExecute(() -> {
                    setConfig(COMMON.poppetShelf.chunkLoading, true);
                    PoppetShelfChunkLoader.onConfigReload(level.getServer());
                })
                .thenWaitUntil(() -> assertLoaded(helper, pos, true))
                .thenExecute(() -> {
                    setConfig(COMMON.poppetShelf.chunkLoading, false);
                    PoppetShelfChunkLoader.onConfigReload(level.getServer());
                })
                .thenWaitUntil(() -> assertLoaded(helper, pos, false))
                .thenExecute(() -> {
                    disconnect(player);
                    setConfig(COMMON.poppetShelf.chunkLoading, true);
                    makePlayer(helper, new Vec3(1.5, 1, 1.5), player.getUUID());
                })
                .thenWaitUntil(() -> assertLoaded(helper, pos, true))
                .thenExecute(() -> PoppetShelfChunkLoader.setShelfOwner(level, pos, null))
                .thenWaitUntil(() -> assertLoaded(helper, pos, false))
                .thenSucceed();
    }

    private static void forgetsMissingShelf(GameTestHelper helper) {
        final ServerLevel level = helper.getLevel();
        final ServerPlayer player = makePlayer(helper);
        final BlockPos pos = farPos(helper, 4000);
        PoppetShelfChunkLoader.setShelfOwner(level, pos, player.getUUID());
        helper.startSequence()
                .thenWaitUntil(() -> assertLoaded(helper, pos, true))
                .thenWaitUntil(() -> assertLoaded(helper, pos, false))
                .thenExecute(() -> {
                    disconnect(player);
                    makePlayer(helper, new Vec3(1.5, 1, 1.5), player.getUUID());
                })
                .thenIdle(20)
                .thenExecute(() -> assertLoaded(helper, pos, false))
                .thenSucceed();
    }

    /**
     * Places a shelf without an owner far away from the test area.
     */
    private static BlockPos farShelf(GameTestHelper helper, int distance) {
        final BlockPos pos = farPos(helper, distance);
        helper.getLevel().setBlockAndUpdate(pos, BlockRegistry.poppetShelf.get().defaultBlockState());
        return pos;
    }

    private static BlockPos farPos(GameTestHelper helper, int distance) {
        final ChunkPos chunkPos = new ChunkPos(helper.absolutePos(BlockPos.ZERO).east(distance));
        return chunkPos.getMiddleBlockPosition(helper.absolutePos(BlockPos.ZERO).getY());
    }

    private static void assertLoaded(GameTestHelper helper, BlockPos pos, boolean loaded) {
        final String debugData = helper.getLevel().getChunkSource().getChunkDebugData(new ChunkPos(pos));
        final boolean isLoaded = debugData.startsWith(ChunkLevel.byStatus(FullChunkStatus.FULL) + "\n");
        helper.assertTrue(isLoaded == loaded, "Chunk at " + pos.toShortString() + " should " + (loaded ? "" : "not ") + "be loaded, ticket level is " + debugData.lines().findFirst().orElse(""));
    }
}
