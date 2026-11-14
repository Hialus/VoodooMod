package de.morrien.voodoo.gametest;

import de.morrien.voodoo.item.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static de.morrien.voodoo.Poppet.PoppetType.VOODOO;
import static de.morrien.voodoo.Poppet.PoppetType.VOODOO_PROTECTION;
import static de.morrien.voodoo.VoodooConfig.COMMON;
import static de.morrien.voodoo.gametest.TestUtil.*;

public class VoodooPoppetTests {
    public static List<TestFunction> tests() {
        return List.of(
                test("voodoo_poppet.needle_hurts_bound_player", VoodooPoppetTests::needleHurtsBoundPlayer),
                test("voodoo_poppet.push_moves_bound_player", VoodooPoppetTests::pushMovesBoundPlayer),
                test("voodoo_poppet.fire_ignites_bound_player", VoodooPoppetTests::fireIgnitesBoundPlayer),
                test("voodoo_poppet.fire_resistance_keeps_durability", VoodooPoppetTests::fireResistanceKeepsDurability),
                test("voodoo_poppet.water_drowns_bound_player", 200, VoodooPoppetTests::waterDrownsBoundPlayer),
                test("voodoo_poppet.never_despawns", VoodooPoppetTests::neverDespawns),
                test("voodoo_poppet.protection_blocks_needle", VoodooPoppetTests::protectionBlocksNeedle),
                test("voodoo_poppet.protection_blocks_push", VoodooPoppetTests::protectionBlocksPush)
        );
    }

    private static void needleHurtsBoundPlayer(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final ServerPlayer target = makePlayer(helper);
        final ItemStack poppet = holdPoppet(player, target, true);
        pull(helper, player, poppet);
        helper.assertValueEqual(target.getHealth(), target.getMaxHealth() - COMMON.voodoo.needleDamage.get(), "bound player health");
        helper.assertValueEqual(player.getOffhandItem().getCount(), 1, "needle count");
        assertPoppetUsed(helper, poppet, VOODOO, COMMON.voodoo.needleDurabilityCost.get());
        helper.succeed();
    }

    private static void pushMovesBoundPlayer(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final ServerPlayer target = makePlayer(helper);
        final ItemStack poppet = holdPoppet(player, target, false);
        player.setYRot(0);
        player.setXRot(0);
        pull(helper, player, poppet);
        helper.assertTrue(target.getDeltaMovement().distanceTo(new Vec3(0, 0, 1.5)) < 1.0E-6, "Bound player should be pushed south, motion is " + target.getDeltaMovement());
        assertPoppetUsed(helper, poppet, VOODOO, COMMON.voodoo.pushDurabilityCost.get());
        helper.succeed();
    }

    private static void fireIgnitesBoundPlayer(GameTestHelper helper) {
        final ServerPlayer target = makePlayer(helper);
        final ItemEntity entity = dropInto(helper, boundPoppet(VOODOO, target), Blocks.LAVA);
        helper.onEachTick(target::doTick);
        helper.succeedWhen(() -> {
            helper.assertTrue(target.isOnFire(), "Bound player should be on fire");
            helper.assertTrue(target.getHealth() < target.getMaxHealth(), "Bound player should be hurt by burning");
            assertPoppetUsed(helper, entity.getItem(), VOODOO, COMMON.voodoo.fireDurabilityCost.get());
        });
    }

    private static void fireResistanceKeepsDurability(GameTestHelper helper) {
        final ServerPlayer target = makePlayer(helper);
        target.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200));
        final ItemEntity entity = dropInto(helper, boundPoppet(VOODOO, target), Blocks.LAVA);
        helper.startSequence()
                .thenWaitUntil(() -> helper.assertTrue(entity.isInLava(), "Poppet should be in lava"))
                .thenIdle(10)
                .thenExecute(() -> {
                    helper.assertFalse(target.isOnFire(), "Bound player should not be on fire");
                    assertFullHealth(helper, target);
                    helper.assertValueEqual(entity.getItem().getDamageValue(), 0, "poppet damage");
                })
                .thenSucceed();
    }

    private static void waterDrownsBoundPlayer(GameTestHelper helper) {
        final ServerPlayer target = makePlayer(helper);
        final ItemEntity entity = dropInto(helper, boundPoppet(VOODOO, target), Blocks.WATER);
        helper.succeedWhen(() -> {
            helper.assertValueEqual(target.getHealth(), target.getMaxHealth() - 2, "bound player health");
            assertPoppetUsed(helper, entity.getItem(), VOODOO, COMMON.voodoo.drownDurabilityCost.get());
        });
    }

    private static void neverDespawns(GameTestHelper helper) {
        final ItemEntity poppet = dropItem(helper, poppet(VOODOO), new Vec3(1.5, 1, 1.5));
        final ItemEntity stick = dropItem(helper, new ItemStack(Items.STICK), new Vec3(3.5, 1, 1.5));
        poppet.makeFakeItem();
        stick.makeFakeItem();
        helper.runAfterDelay(5, () -> {
            helper.assertTrue(stick.isRemoved(), "Stick should despawn");
            helper.assertFalse(poppet.isRemoved(), "Voodoo poppet should not despawn");
            helper.succeed();
        });
    }

    private static void protectionBlocksNeedle(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final ServerPlayer target = makePlayer(helper);
        final ItemStack poppet = holdPoppet(player, target, true);
        final ItemStack protection = boundPoppet(VOODOO_PROTECTION, target);
        target.setItemInHand(InteractionHand.MAIN_HAND, protection);
        pull(helper, player, poppet);
        assertFullHealth(helper, target);
        helper.assertTrue(poppet.isEmpty(), "Voodoo poppet should break");
        assertPoppetUsed(helper, protection, VOODOO_PROTECTION, 1);
        helper.succeed();
    }

    private static void protectionBlocksPush(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final ServerPlayer target = makePlayer(helper);
        final ItemStack poppet = holdPoppet(player, target, false);
        final ItemStack protection = boundPoppet(VOODOO_PROTECTION, target);
        target.setItemInHand(InteractionHand.MAIN_HAND, protection);
        pull(helper, player, poppet);
        helper.assertValueEqual(target.getDeltaMovement(), Vec3.ZERO, "bound player motion");
        helper.assertTrue(poppet.isEmpty(), "Voodoo poppet should break");
        assertPoppetUsed(helper, protection, VOODOO_PROTECTION, 1);
        helper.succeed();
    }

    private static ItemStack holdPoppet(ServerPlayer player, ServerPlayer target, boolean needle) {
        final ItemStack poppet = boundPoppet(VOODOO, target);
        player.setItemInHand(InteractionHand.MAIN_HAND, poppet);
        if (needle)
            player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(ItemRegistry.needle.get(), 2));
        return poppet;
    }

    private static void pull(GameTestHelper helper, ServerPlayer player, ItemStack poppet) {
        poppet.releaseUsing(helper.getLevel(), player, 72000 - COMMON.voodoo.pullDuration.get());
    }

    private static ItemEntity dropInto(GameTestHelper helper, ItemStack stack, Block fluid) {
        final BlockPos pos = new BlockPos(5, 1, 5);
        helper.setBlock(pos.below(), Blocks.STONE);
        helper.setBlock(pos, fluid);
        return dropItem(helper, stack, Vec3.atBottomCenterOf(pos));
    }
}
