package de.morrien.voodoo.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.morrien.voodoo.tileentity.PoppetShelfTileEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.server.ServerWorld;

public class ListShelvesCommand {
    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands
                .literal("shelves")
                .then(Commands.argument("player", EntityArgument.player()).requires(cs -> cs.hasPermission(3)).executes(context -> {
                    final ServerPlayerEntity player = EntityArgument.getPlayer(context, "player");
                    return list(context, player);
                }))
                .executes(context -> {
                    final ServerPlayerEntity player = context.getSource().getPlayerOrException();
                    return list(context, player);
                });
    }

    private static int list(CommandContext<CommandSource> context, ServerPlayerEntity player) {
        TextComponent message = new StringTextComponent("");
        message.append(new TranslationTextComponent(
                "commands.voodoo.list.shelves.header",
                player.getDisplayName())
                .setStyle(new Style().setColor(TextFormatting.GREEN).setBold(true))
        );
        CommandSource source = context.getSource();
        int counter = 1;
        for (ServerWorld world : player.server.getAllLevels()) {
            for (TileEntity tileEntity : world.blockEntityList) {
                if (tileEntity instanceof PoppetShelfTileEntity) {
                    if (player.getUUID().equals(((PoppetShelfTileEntity) tileEntity).getOwnerUuid())) {
                        message.append("\n");
                        final TranslationTextComponent text = new TranslationTextComponent(
                                "commands.voodoo.list.shelves.line",
                                counter,
                                tileEntity.getBlockPos().getX(),
                                tileEntity.getBlockPos().getY(),
                                tileEntity.getBlockPos().getZ(),
                                world.dimension.getType().getRegistryName().getPath()
                        );
                        Style style = new Style()
                                .setClickEvent(new ClickEvent(
                                        ClickEvent.Action.SUGGEST_COMMAND,
                                        "/execute in " + world.dimension.getType().getRegistryName().toString() + " run tp " + player.getName().getString() + " " + tileEntity.getBlockPos().getX() + " " + tileEntity.getBlockPos().getY() + " " + tileEntity.getBlockPos().getZ()
                                ))
                                .setHoverEvent(new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        new TranslationTextComponent("commands.voodoo.list.teleport")
                                ));
                        text.setStyle(style);
                        message.append(text);
                        counter++;
                    }
                }
            }
        }
        if (counter == 1) {
            message = new TranslationTextComponent("commands.voodoo.list.shelves.none");
            message.setStyle(new Style().setColor(TextFormatting.RED));
        }
        source.sendSuccess(message, false);
        return 0;
    }
}
