package de.morrien.voodoo.gametest;

import de.morrien.voodoo.util.BindingUtil;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static de.morrien.voodoo.Poppet.PoppetType.FALL_PROTECTION;
import static de.morrien.voodoo.Poppet.PoppetType.VOODOO;
import static de.morrien.voodoo.gametest.TestUtil.*;

public class CommandTests {
    public static List<TestFunction> tests() {
        return List.of(
                test("command.bind_self", CommandTests::bindSelf),
                test("command.bind_other_player", CommandTests::bindOtherPlayer),
                test("command.bind_rejects_other_items", CommandTests::bindRejectsOtherItems),
                test("command.unbind", CommandTests::unbind),
                test("command.list_poppets", CommandTests::listPoppets),
                test("command.list_shelves", CommandTests::listShelves)
        );
    }

    private static void bindSelf(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final ItemStack poppet = poppet(VOODOO);
        player.setItemInHand(InteractionHand.MAIN_HAND, poppet);
        assertMessage(helper, run(helper, player, "voodoo bind"), "commands.voodoo.bind.success");
        assertBoundTo(helper, poppet, player);
        helper.succeed();
    }

    private static void bindOtherPlayer(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final ServerPlayer target = makePlayer(helper);
        final ItemStack poppet = poppet(VOODOO);
        player.setItemInHand(InteractionHand.MAIN_HAND, poppet);
        assertMessage(helper, run(helper, player, "voodoo bind " + target.getName().getString()), "commands.voodoo.bind.success");
        assertBoundTo(helper, poppet, target);
        helper.succeed();
    }

    private static void bindRejectsOtherItems(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final ItemStack stack = new ItemStack(Items.STONE);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        assertMessage(helper, run(helper, player, "voodoo bind"), "commands.voodoo.bind.noitem");
        helper.assertFalse(BindingUtil.isBound(stack), "Stone should not be bound");
        helper.succeed();
    }

    private static void unbind(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final ItemStack poppet = boundPoppet(VOODOO, player);
        player.setItemInHand(InteractionHand.MAIN_HAND, poppet);
        assertMessage(helper, run(helper, player, "voodoo unbind"), "commands.voodoo.unbind.success");
        helper.assertFalse(BindingUtil.isBound(poppet), "Poppet should be unbound");
        helper.succeed();
    }

    private static void listPoppets(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        assertMessage(helper, run(helper, player, "voodoo list poppets"), "commands.voodoo.list.poppets.none");
        player.getInventory().setItem(9, boundPoppet(FALL_PROTECTION, player));
        placeShelf(helper, player, new BlockPos(3, 1, 3)).setItem(0, boundPoppet(FALL_PROTECTION, player));
        final List<Component> messages = run(helper, player, "voodoo list poppets");
        assertMessage(helper, messages, "commands.voodoo.list.poppets.line.inventory");
        assertMessage(helper, messages, "commands.voodoo.list.poppets.line.shelf");
        helper.succeed();
    }

    private static void listShelves(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        assertMessage(helper, run(helper, player, "voodoo list shelves"), "commands.voodoo.list.shelves.none");
        placeShelf(helper, player, new BlockPos(3, 1, 3));
        assertMessage(helper, run(helper, player, "voodoo list shelves"), "commands.voodoo.list.shelves.line");
        helper.succeed();
    }

    /**
     * Runs a command as the player with operator permissions.
     *
     * @return The messages the command sent back
     */
    private static List<Component> run(GameTestHelper helper, ServerPlayer player, String command) {
        final List<Component> messages = new ArrayList<>();
        final CommandSource recorder = new CommandSource() {
            @Override
            public void sendSystemMessage(@NotNull Component component) {
                messages.add(component);
            }

            @Override
            public boolean acceptsSuccess() {
                return true;
            }

            @Override
            public boolean acceptsFailure() {
                return true;
            }

            @Override
            public boolean shouldInformAdmins() {
                return false;
            }
        };
        final CommandSourceStack source = player.createCommandSourceStack().withSource(recorder).withPermission(4);
        helper.getLevel().getServer().getCommands().performPrefixedCommand(source, command);
        return messages;
    }

    private static void assertMessage(GameTestHelper helper, List<Component> messages, String key) {
        helper.assertTrue(messages.stream().anyMatch(message -> containsKey(message, key)), "Expected a message with " + key + ", got " + messages);
    }

    private static boolean containsKey(Component component, String key) {
        if (component.getContents() instanceof TranslatableContents translatable) {
            if (translatable.getKey().equals(key)) return true;
            for (Object arg : translatable.getArgs())
                if (arg instanceof Component argComponent && containsKey(argComponent, key)) return true;
        }
        return component.getSiblings().stream().anyMatch(sibling -> containsKey(sibling, key));
    }
}
