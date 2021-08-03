package de.morrien.voodoo.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.tileentity.PoppetShelfTileEntity;
import de.morrien.voodoo.util.PoppetUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListPoppetsCommand {
    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands
                .literal("poppets")
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
                "commands.voodoo.list.poppets.header",
                player.getDisplayName())
                .setStyle(Style.EMPTY.withColor(TextFormatting.GREEN).withBold(true))
        );
        CommandSource source = context.getSource();
        int counter = 1;
        final List<Poppet> poppets = new ArrayList<>();
        poppets.addAll(PoppetUtil.getPoppetsInInventory(player));
        poppets.addAll(PoppetUtil.getPoppetsInShelves(player));
        for (Poppet poppet : poppets) {
            final ItemStack stack = poppet.getStack();
            final Optional<PoppetShelfTileEntity> poppetShelf = poppet.getPoppetShelf();
            message.append("\n");
            final TranslationTextComponent poppetText = new TranslationTextComponent(stack.getItem().getDescriptionId());
            if (poppetShelf.isPresent()) {
                final PoppetShelfTileEntity tileEntity = poppetShelf.get();
                final World world = tileEntity.getLevel();
                TranslationTextComponent text = new TranslationTextComponent(
                        "commands.voodoo.list.poppets.line.shelf",
                        counter,
                        poppetText,
                        world.dimension().location().getPath(),
                        tileEntity.getBlockPos().getX(),
                        tileEntity.getBlockPos().getY(),
                        tileEntity.getBlockPos().getZ()
                );
                Style style = Style.EMPTY
                        .withClickEvent(new ClickEvent(
                                ClickEvent.Action.SUGGEST_COMMAND,
                                "/execute in " + world.dimension().location().toString() + " run tp " + player.getName().getString() + " " + tileEntity.getBlockPos().getX() + " " + tileEntity.getBlockPos().getY() + " " + tileEntity.getBlockPos().getZ()
                        ))
                        .withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                new TranslationTextComponent("commands.voodoo.list.teleport")
                        ));
                text.setStyle(style);
                message.append(text);
            } else {
                message.append(new TranslationTextComponent(
                        "commands.voodoo.list.poppets.line.inventory",
                        counter,
                        poppetText
                ));
            }
            counter++;
        }
        if (counter == 1) {
            message = new TranslationTextComponent("commands.voodoo.list.poppets.none");
            message.setStyle(Style.EMPTY.withColor(TextFormatting.RED));
        }
        source.sendSuccess(message, false);
        return 0;
    }
}
