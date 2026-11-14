package de.morrien.voodoo.gametest;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.util.BindingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static de.morrien.voodoo.Poppet.PoppetType.*;
import static de.morrien.voodoo.gametest.TestUtil.*;

public class ProtectionPoppetTests {
    public static List<TestFunction> tests() {
        return List.of(
                test("protection_poppet.fall", ProtectionPoppetTests::fall),
                test("protection_poppet.fire", ProtectionPoppetTests::fire),
                test("protection_poppet.lava", ProtectionPoppetTests::lava),
                test("protection_poppet.drowning", ProtectionPoppetTests::drowning),
                test("protection_poppet.explosion", ProtectionPoppetTests::explosion),
                test("protection_poppet.projectile", ProtectionPoppetTests::projectile),
                test("protection_poppet.void", ProtectionPoppetTests::fellOutOfWorld),
                test("protection_poppet.wither", ProtectionPoppetTests::wither),
                test("protection_poppet.potion", ProtectionPoppetTests::potion),
                test("protection_poppet.hunger", ProtectionPoppetTests::hunger),
                test("protection_poppet.death", ProtectionPoppetTests::death),
                test("protection_poppet.ignores_other_players_poppet", ProtectionPoppetTests::ignoresOtherPlayersPoppet)
        );
    }

    private static void fall(GameTestHelper helper) {
        assertProtects(helper, FALL_PROTECTION, DamageSources::fall, 5, 5);
        helper.succeed();
    }

    private static void fire(GameTestHelper helper) {
        final ServerPlayer player = assertProtects(helper, FIRE_PROTECTION, DamageSources::inFire, 1, 1);
        helper.assertTrue(player.hasEffect(MobEffects.FIRE_RESISTANCE), "Player should get fire resistance");
        helper.succeed();
    }

    private static void lava(GameTestHelper helper) {
        assertProtects(helper, FIRE_PROTECTION, DamageSources::lava, 4, 1);
        helper.succeed();
    }

    private static void drowning(GameTestHelper helper) {
        final ServerPlayer player = assertProtects(helper, WATER_PROTECTION, DamageSources::drown, 2, 1);
        helper.assertValueEqual(player.getAirSupply(), player.getMaxAirSupply(), "air supply");
        helper.assertTrue(player.hasEffect(MobEffects.WATER_BREATHING), "Player should get water breathing");
        helper.succeed();
    }

    private static void explosion(GameTestHelper helper) {
        assertProtects(helper, EXPLOSION_PROTECTION, sources -> sources.explosion(null, null), 5, 1);
        helper.succeed();
    }

    private static void projectile(GameTestHelper helper) {
        final Arrow arrow = helper.spawn(EntityType.ARROW, new BlockPos(3, 1, 3));
        assertProtects(helper, PROJECTILE_PROTECTION, sources -> sources.arrow(arrow, null), 5, 1);
        helper.assertTrue(arrow.isRemoved(), "Arrow should be removed");
        helper.succeed();
    }

    private static void fellOutOfWorld(GameTestHelper helper) {
        final ServerPlayer player = assertProtects(helper, VOID_PROTECTION, DamageSources::fellOutOfWorld, 4, 1);
        helper.assertValueEqual(player.blockPosition(), helper.getLevel().getSharedSpawnPos().above(), "player position");
        helper.succeed();
    }

    private static void wither(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final ItemStack poppet = holdPoppet(player, WITHER_PROTECTION);
        player.addEffect(new MobEffectInstance(MobEffects.WITHER, 200));
        helper.onEachTick(player::doTick);
        helper.succeedWhen(() -> {
            helper.assertFalse(player.hasEffect(MobEffects.WITHER), "Wither should be removed");
            assertPoppetUsed(helper, poppet, WITHER_PROTECTION, 1);
        });
    }

    private static void potion(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final ItemStack poppet = holdPoppet(player, POTION_PROTECTION);
        player.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 1));
        helper.onEachTick(player::doTick);
        helper.succeedWhen(() -> {
            helper.assertFalse(player.hasEffect(MobEffects.POISON), "Poison should be removed");
            assertPoppetUsed(helper, poppet, POTION_PROTECTION, 2);
        });
    }

    private static void hunger(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final ItemStack poppet = holdPoppet(player, HUNGER_PROTECTION);
        player.getFoodData().setFoodLevel(5);
        helper.onEachTick(player::doTick);
        helper.succeedWhen(() -> {
            helper.assertTrue(player.hasEffect(MobEffects.SATURATION), "Player should get saturation");
            assertPoppetUsed(helper, poppet, HUNGER_PROTECTION, 1);
        });
    }

    private static void death(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final ItemStack poppet = holdPoppet(player, DEATH_PROTECTION);
        player.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 100);
        helper.assertFalse(player.isDeadOrDying(), "Player should survive");
        helper.assertValueEqual(player.getHealth(), player.getMaxHealth() / 2, "player health");
        helper.assertTrue(player.hasEffect(MobEffects.REGENERATION), "Player should get regeneration");
        assertPoppetUsed(helper, poppet, DEATH_PROTECTION, 1);
        helper.succeed();
    }

    private static void ignoresOtherPlayersPoppet(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final ItemStack poppet = poppet(FALL_PROTECTION);
        BindingUtil.bind(poppet, UUID.randomUUID(), "other");
        player.getInventory().setItem(9, poppet);
        player.hurtServer(helper.getLevel(), helper.getLevel().damageSources().fall(), 5);
        helper.assertValueEqual(player.getHealth(), player.getMaxHealth() - 5, "player health");
        helper.assertValueEqual(poppet.getDamageValue(), 0, "poppet damage");
        helper.succeed();
    }

    /**
     * Hurts a player that carries a protection poppet and checks that the damage is blocked.
     */
    private static ServerPlayer assertProtects(GameTestHelper helper, Poppet.PoppetType poppetType, Function<DamageSources, DamageSource> source, float amount, int durabilityCost) {
        final ServerPlayer player = makePlayer(helper);
        final ItemStack poppet = holdPoppet(player, poppetType);
        player.hurtServer(helper.getLevel(), source.apply(helper.getLevel().damageSources()), amount);
        assertFullHealth(helper, player);
        assertPoppetUsed(helper, poppet, poppetType, durabilityCost);
        return player;
    }

    private static ItemStack holdPoppet(ServerPlayer player, Poppet.PoppetType poppetType) {
        final ItemStack poppet = boundPoppet(poppetType, player);
        player.getInventory().setItem(9, poppet);
        return poppet;
    }
}
