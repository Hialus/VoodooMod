package de.morrien.voodoo.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.blockentity.PoppetShelfBlockEntity;
import de.morrien.voodoo.util.PoppetUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListPoppetsCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands
                .literal("poppets")
                .then(Commands.argument("player", EntityArgument.player()).requires(cs -> cs.hasPermission(3)).executes(context -> {
                    final ServerPlayer player = EntityArgument.getPlayer(context, "player");
                    return list(context, player);
                }))
                .executes(context -> {
                    final ServerPlayer player = context.getSource().getPlayerOrException();
                    return list(context, player);
                });
    }

    private static int list(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        var message = Component.literal("");
        message.append(Component.translatable(
                        "commands.voodoo.list.poppets.header",
                        player.getDisplayName())
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withBold(true))
        );
        CommandSourceStack source = context.getSource();
        int counter = 1;
        final List<Poppet> poppets = new ArrayList<>();
        poppets.addAll(PoppetUtil.getPoppetsInInventory(player));
        poppets.addAll(PoppetUtil.getPoppetsInShelves(player));
        for (Poppet poppet : poppets) {
            final ItemStack stack = poppet.getStack();
            final Optional<PoppetShelfBlockEntity> poppetShelf = poppet.getPoppetShelf();
            message.append("\n");
            if (poppetShelf.isPresent()) {
                final PoppetShelfBlockEntity blockEntity = poppetShelf.get();
                final Level world = blockEntity.getLevel();
                var text = Component.translatable(
                        "commands.voodoo.list.poppets.line.shelf",
                        counter,
                        stack.getDisplayName(),
                        world.dimension().location().getPath(),
                        blockEntity.getBlockPos().getX(),
                        blockEntity.getBlockPos().getY(),
                        blockEntity.getBlockPos().getZ()
                );
                Style style = Style.EMPTY
                        .withClickEvent(new ClickEvent(
                                ClickEvent.Action.SUGGEST_COMMAND,
                                "/execute in " + world.dimension().location().toString() + " run tp " + player.getName().getString() + " " + blockEntity.getBlockPos().getX() + " " + blockEntity.getBlockPos().getY() + " " + blockEntity.getBlockPos().getZ()
                        ))
                        .withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Component.translatable("commands.voodoo.list.teleport")
                        ));
                text.setStyle(style);
                message.append(text);
            } else {
                message.append(Component.translatable(
                        "commands.voodoo.list.poppets.line.inventory",
                        counter,
                        stack.getDisplayName()
                ));
            }
            counter++;
        }
        if (counter == 1) {
            message = Component.translatable("commands.voodoo.list.poppets.none");
            message.setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
        }
        source.sendSuccess(message, false);
        return 0;
    }
}
