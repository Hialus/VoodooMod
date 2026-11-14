package de.morrien.voodoo.gametest;

import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.util.BindingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static de.morrien.voodoo.gametest.TestUtil.*;

public class TaglockKitTests {
    public static List<TestFunction> tests() {
        return List.of(
                test("taglock_kit.binds_self", TaglockKitTests::bindsSelf),
                test("taglock_kit.binds_other_player", TaglockKitTests::bindsOtherPlayer),
                test("taglock_kit.binds_bed_owner", TaglockKitTests::bindsBedOwner),
                test("taglock_kit.ignores_bed_in_other_dimension", TaglockKitTests::ignoresBedInOtherDimension)
        );
    }

    private static void bindsSelf(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final ItemStack taglock = new ItemStack(ItemRegistry.taglockKit.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, taglock);
        player.setShiftKeyDown(true);
        taglock.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        assertBoundTo(helper, taglock, player);
        helper.succeed();
    }

    private static void bindsOtherPlayer(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper, new Vec3(1.5, 1, 1.5));
        final ServerPlayer target = makePlayer(helper, new Vec3(3.5, 1, 1.5));
        final ItemStack taglock = new ItemStack(ItemRegistry.taglockKit.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, taglock);
        player.connection.handleInteract(ServerboundInteractPacket.createInteractionPacket(target, false, InteractionHand.MAIN_HAND));
        assertBoundTo(helper, taglock, target);
        helper.succeed();
    }

    private static void bindsBedOwner(GameTestHelper helper) {
        final ServerPlayer target = makePlayer(helper);
        assertBoundTo(helper, useOnBed(helper, target, helper.getLevel().dimension()), target);
        helper.succeed();
    }

    private static void ignoresBedInOtherDimension(GameTestHelper helper) {
        final ServerPlayer target = makePlayer(helper);
        helper.assertFalse(BindingUtil.isBound(useOnBed(helper, target, Level.NETHER)), "Taglock kit should not be bound");
        helper.succeed();
    }

    /**
     * Create a bed, set the players respawn point to it, and then use a taglock kit on it.
     */
    private static ItemStack useOnBed(GameTestHelper helper, ServerPlayer target, ResourceKey<Level> dimension) {
        final ServerPlayer player = makePlayer(helper);
        final BlockPos head = new BlockPos(5, 1, 5);
        final BlockPos foot = head.relative(Direction.SOUTH);
        helper.setBlock(head, Blocks.RED_BED.defaultBlockState().setValue(BedBlock.FACING, Direction.NORTH).setValue(BedBlock.PART, BedPart.HEAD));
        helper.setBlock(foot, Blocks.RED_BED.defaultBlockState().setValue(BedBlock.FACING, Direction.NORTH).setValue(BedBlock.PART, BedPart.FOOT));
        target.setRespawnPosition(dimension, helper.absolutePos(head), 0, false, false);
        final ItemStack taglock = new ItemStack(ItemRegistry.taglockKit.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, taglock);
        player.setShiftKeyDown(true);
        final BlockPos clicked = helper.absolutePos(foot);
        taglock.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(clicked), Direction.UP, clicked, false)));
        return taglock;
    }
}
