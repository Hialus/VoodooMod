package de.morrien.voodoo.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

import static de.morrien.voodoo.Poppet.PoppetType.VOODOO;
import static de.morrien.voodoo.gametest.TestUtil.*;

public class RepairTests {
    public static List<TestFunction> tests() {
        return List.of(
                test("repair.anvil_blocked", RepairTests::anvilBlocked),
                test("repair.grindstone_blocked", RepairTests::grindstoneBlocked),
                test("repair.crafting_blocked", RepairTests::craftingBlocked)
        );
    }

    private static void anvilBlocked(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final AnvilMenu menu = new AnvilMenu(1, player.getInventory(), access(helper));
        player.containerMenu = menu;
        menu.getSlot(AnvilMenu.INPUT_SLOT).set(damagedPoppet());
        menu.setItemName("Renamed");
        helper.assertTrue(menu.getSlot(AnvilMenu.RESULT_SLOT).hasItem(), "Renaming a poppet should give a result");
        menu.getSlot(AnvilMenu.ADDITIONAL_SLOT).set(damagedPoppet());
        helper.assertFalse(menu.getSlot(AnvilMenu.RESULT_SLOT).hasItem(), "Two poppets of the same type should give no result");
        helper.succeed();
    }

    private static void grindstoneBlocked(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final GrindstoneMenu menu = new GrindstoneMenu(1, player.getInventory(), access(helper));
        player.containerMenu = menu;
        menu.getSlot(GrindstoneMenu.INPUT_SLOT).set(damaged(Items.IRON_SWORD));
        menu.getSlot(GrindstoneMenu.ADDITIONAL_SLOT).set(damaged(Items.IRON_SWORD));
        helper.assertTrue(menu.getSlot(GrindstoneMenu.RESULT_SLOT).hasItem(), "Two damaged swords should give a result");
        menu.getSlot(GrindstoneMenu.INPUT_SLOT).set(damagedPoppet());
        menu.getSlot(GrindstoneMenu.ADDITIONAL_SLOT).set(damagedPoppet());
        helper.assertFalse(menu.getSlot(GrindstoneMenu.RESULT_SLOT).hasItem(), "Two poppets of the same type should give no result");
        helper.succeed();
    }

    private static void craftingBlocked(GameTestHelper helper) {
        helper.assertFalse(craft(helper.getLevel(), damaged(Items.IRON_SWORD), damaged(Items.IRON_SWORD)).isEmpty(), "Two damaged swords should give a result");
        helper.assertTrue(craft(helper.getLevel(), damagedPoppet(), damagedPoppet()).isEmpty(), "Two poppets of the same type should give no result");
        helper.succeed();
    }

    private static ContainerLevelAccess access(GameTestHelper helper) {
        return ContainerLevelAccess.create(helper.getLevel(), helper.absolutePos(BlockPos.ZERO));
    }

    private static ItemStack damagedPoppet() {
        final ItemStack stack = poppet(VOODOO);
        stack.setDamageValue(1);
        return stack;
    }

    private static ItemStack damaged(Item item) {
        final ItemStack stack = new ItemStack(item);
        stack.setDamageValue(10);
        return stack;
    }
}
