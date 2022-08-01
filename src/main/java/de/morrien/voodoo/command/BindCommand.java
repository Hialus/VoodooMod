package de.morrien.voodoo.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.item.PoppetItem;
import de.morrien.voodoo.util.BindingUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class BindCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands
                .literal("bind")
                .then(Commands
                        .argument("player", EntityArgument.player())
                        .requires(cs -> cs.hasPermission(3))
                        .executes(context -> {
                            final ServerPlayer player = EntityArgument.getPlayer(context, "player");
                            return bind(context, player);
                        })
                )
                .requires(cs -> cs.hasPermission(3))
                .executes(context -> {
                    final ServerPlayer player = context.getSource().getPlayerOrException();
                    return bind(context, player);
                });
    }

    private static int bind(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        final ServerPlayer player = source.getPlayerOrException();
        final ItemStack itemStack = player.getMainHandItem();
        boolean isPoppet = itemStack.getItem() instanceof PoppetItem;
        boolean isBlankPoppet = itemStack.getItem() == ItemRegistry.poppetMap.get(Poppet.PoppetType.BLANK).get();
        boolean isTaglockKit = itemStack.getItem() == ItemRegistry.taglockKit.get();
        if (!(isPoppet || isTaglockKit) || isBlankPoppet) {
            final var text = Component.translatable("commands.voodoo.bind.noitem");
            throw new CommandSyntaxException(new SimpleCommandExceptionType(text), text);
        }
        BindingUtil.bind(itemStack, target);
        final var text = Component.translatable(
                "commands.voodoo.bind.success",
                itemStack.getDisplayName(),
                target.getDisplayName()
        );
        text.setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));
        source.sendSuccess(text, true);
        return 0;
    }
}
