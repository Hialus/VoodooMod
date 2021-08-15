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
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class UnbindCommand {
    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands
                .literal("unbind")
                .executes(UnbindCommand::bind);
    }

    private static int bind(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        final ServerPlayerEntity player = source.getPlayerOrException();
        final ItemStack itemStack = player.getMainHandItem();
        boolean isPoppet = itemStack.getItem() instanceof PoppetItem;
        boolean isBlankPoppet = itemStack.getItem() == ItemRegistry.poppetMap.get(Poppet.PoppetType.BLANK).get();
        boolean isTaglockKit = itemStack.getItem() == ItemRegistry.taglockKit.get();
        if (!(isPoppet || isTaglockKit) || isBlankPoppet) {
            final TranslationTextComponent text = new TranslationTextComponent("commands.voodoo.unbind.noitem");
            throw new CommandSyntaxException(new SimpleCommandExceptionType(text), text);
        }
        BindingUtil.unbind(itemStack);
        final TranslationTextComponent text = new TranslationTextComponent(
                "commands.voodoo.unbind.success",
                itemStack.getDisplayName()
        );
        text.setStyle(new Style().setColor(TextFormatting.GREEN));
        source.sendSuccess(text, true);
        return 0;
    }
}
