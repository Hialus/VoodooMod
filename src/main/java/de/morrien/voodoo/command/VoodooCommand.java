package de.morrien.voodoo.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class VoodooCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> voodooCommand = dispatcher.register(
                Commands.literal("voodoo")
                        .then(
                                Commands.literal("list")
                                        .then(ListPoppetsCommand.register(dispatcher))
                                        .then(ListShelvesCommand.register(dispatcher))
                        )
        );
    }
}
