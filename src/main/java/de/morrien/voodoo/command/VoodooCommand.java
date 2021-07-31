package de.morrien.voodoo.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class VoodooCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> voodooCommand = dispatcher.register(
                Commands.literal("voodoo")
                        .then(
                                Commands.literal("list")
                                        .then(ListPoppetsCommand.register(dispatcher))
                                        .then(ListShelvesCommand.register(dispatcher))
                        )
        );
    }
}
