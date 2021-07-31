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
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.server.ServerWorld;

public class ListShelvesCommand {
    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands
                .literal("shelves")
                .then(Commands.argument("player", EntityArgument.players()).requires(cs -> cs.hasPermission(3)).executes(context -> {
                    final ServerPlayerEntity player = EntityArgument.getPlayer(context, "player");
                    return list(context, player);
                }))
                .executes(context -> {
                    final ServerPlayerEntity player = context.getSource().getPlayerOrException();
                    return list(context, player);
                });
    }

    private static int list(CommandContext<CommandSource> context, ServerPlayerEntity player) {
        StringTextComponent message = new StringTextComponent("Shelves of " + player.getDisplayName().getString() + ":");
        CommandSource source = context.getSource();
        int counter = 1;
        for (ServerWorld world : player.server.getAllLevels()) {
            for (TileEntity tileEntity : world.blockEntityList) {
                if (tileEntity instanceof PoppetShelfTileEntity) {
                    if (player.getUUID().equals(((PoppetShelfTileEntity) tileEntity).owner)) {
                        message.append("\n");
                        final StringTextComponent text = new StringTextComponent(counter + ". " + tileEntity.getBlockPos().getX() + ", " + tileEntity.getBlockPos().getY() + ", " + tileEntity.getBlockPos().getZ() + " (" + world.dimension().location().getPath() + ")");
                        Style style = Style.EMPTY
                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/execute in " + world.dimension().location().toString() + " run tp " + player.getName().getString() + " " + tileEntity.getBlockPos().getX() + " " + tileEntity.getBlockPos().getY() + " " + tileEntity.getBlockPos().getZ()))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Teleport to shelf")));
                        text.setStyle(style);
                        message.append(text);
                        counter++;
                    }
                }
            }
        }
        if (counter == 1) {
            message = new StringTextComponent("No shelves found");
            message.setStyle(Style.EMPTY.withColor(Color.parseColor("#ff0000")));
        }
        source.sendSuccess(message, false);
        return 0;
    }
}
