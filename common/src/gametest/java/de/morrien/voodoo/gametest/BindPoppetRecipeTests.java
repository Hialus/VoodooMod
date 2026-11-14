package de.morrien.voodoo.gametest;

import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.util.BindingUtil;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static de.morrien.voodoo.Poppet.PoppetType.BLANK;
import static de.morrien.voodoo.Poppet.PoppetType.VOODOO;
import static de.morrien.voodoo.gametest.TestUtil.*;

public class BindPoppetRecipeTests {
    public static List<TestFunction> tests() {
        return List.of(
                test("bind_poppet_recipe.binds_poppet", BindPoppetRecipeTests::bindsPoppet),
                test("bind_poppet_recipe.keeps_damage", BindPoppetRecipeTests::keepsDamage),
                test("bind_poppet_recipe.rejects_blank_poppet", BindPoppetRecipeTests::rejectsBlankPoppet),
                test("bind_poppet_recipe.rejects_bound_poppet", BindPoppetRecipeTests::rejectsBoundPoppet)
        );
    }

    private static void bindsPoppet(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final ItemStack result = craft(helper.getLevel(), boundTaglock(player), poppet(VOODOO));
        helper.assertTrue(result.is(ItemRegistry.poppetMap.get(VOODOO).get()), "Result should be a voodoo poppet, is " + result);
        assertBoundTo(helper, result, player);
        helper.succeed();
    }

    private static void keepsDamage(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final ItemStack poppet = poppet(VOODOO);
        poppet.setDamageValue(5);
        helper.assertValueEqual(craft(helper.getLevel(), boundTaglock(player), poppet).getDamageValue(), 5, "poppet damage");
        helper.succeed();
    }

    private static void rejectsBlankPoppet(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        helper.assertTrue(craft(helper.getLevel(), boundTaglock(player), poppet(BLANK)).isEmpty(), "A blank poppet should not be bound");
        helper.succeed();
    }

    private static void rejectsBoundPoppet(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final ServerPlayer other = makePlayer(helper);
        helper.assertTrue(craft(helper.getLevel(), boundTaglock(player), boundPoppet(VOODOO, other)).isEmpty(), "A bound poppet should not be bound again");
        helper.succeed();
    }

    private static ItemStack boundTaglock(ServerPlayer player) {
        final ItemStack taglock = new ItemStack(ItemRegistry.taglockKit.get());
        BindingUtil.bind(taglock, player);
        return taglock;
    }
}
