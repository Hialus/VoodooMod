package de.morrien.voodoo.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static de.morrien.voodoo.Poppet.PoppetType.VAMPIRIC;
import static de.morrien.voodoo.VoodooConfig.COMMON;
import static de.morrien.voodoo.gametest.TestUtil.*;

public class VampiricPoppetTests {
    public static List<TestFunction> tests() {
        return List.of(
                test("vampiric_poppet.drains_bound_player", VampiricPoppetTests::drainsBoundPlayer),
                test("vampiric_poppet.stops_at_health_limit", VampiricPoppetTests::stopsAtHealthLimit)
        );
    }

    private static void drainsBoundPlayer(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final ServerPlayer target = makePlayer(helper);
        player.setHealth(10);
        final ItemStack poppet = drain(helper, player, target);
        final int drained = COMMON.vampiric.healthPerDrain.get();
        helper.assertValueEqual(player.getHealth(), 10F + drained, "player health");
        helper.assertValueEqual(target.getHealth(), target.getMaxHealth() - drained, "bound player health");
        assertPoppetUsed(helper, poppet, VAMPIRIC, 1);
        helper.succeed();
    }

    private static void stopsAtHealthLimit(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final ServerPlayer target = makePlayer(helper);
        player.setHealth(10);
        target.setHealth(COMMON.vampiric.healthLimit.get() + 1);
        drain(helper, player, target);
        helper.assertValueEqual(player.getHealth(), 11F, "player health");
        helper.assertValueEqual(target.getHealth(), (float) COMMON.vampiric.healthLimit.get(), "bound player health");
        drain(helper, player, target);
        helper.assertValueEqual(target.getHealth(), (float) COMMON.vampiric.healthLimit.get(), "bound player health after the second drain");
        helper.succeed();
    }

    private static ItemStack drain(GameTestHelper helper, ServerPlayer player, ServerPlayer target) {
        final ItemStack poppet = boundPoppet(VAMPIRIC, target);
        player.setItemInHand(InteractionHand.MAIN_HAND, poppet);
        poppet.onUseTick(helper.getLevel(), player, 1);
        return poppet;
    }
}
