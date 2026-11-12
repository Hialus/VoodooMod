package de.morrien.voodoo.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.morrien.voodoo.blockentity.PoppetShelfBlockEntity;
import de.morrien.voodoo.util.PoppetUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.stream.Collectors;

public class ListShelvesCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands
                .literal("shelves")
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
                "commands.voodoo.list.shelves.header",
                player.getDisplayName())
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withBold(true))
        );
        CommandSourceStack source = context.getSource();
        int counter = 1;
        for (ServerLevel world : player.server.getAllLevels()) {
            for (BlockEntity blockEntity : PoppetUtil.getPoppetShelvesStream(player.server).collect(Collectors.toList())) {
                if (blockEntity instanceof PoppetShelfBlockEntity) {
                    if (player.getUUID().equals(((PoppetShelfBlockEntity) blockEntity).getOwnerUuid())) {
                        message.append("\n");
                        final var text = Component.translatable(
                                "commands.voodoo.list.shelves.line",
                                counter,
                                blockEntity.getBlockPos().getX(),
                                blockEntity.getBlockPos().getY(),
                                blockEntity.getBlockPos().getZ(),
                                world.dimension().location().getPath()
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
                        counter++;
                    }
                }
            }
        }
        if (counter == 1) {
            message = Component.translatable("commands.voodoo.list.shelves.none");
            message.setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
        }
        source.sendSuccess(message, false);
        return 0;
    }
}
