package de.morrien.voodoo.item;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.VoodooConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;
import javax.annotation.Nullable;

import static de.morrien.voodoo.Poppet.PoppetType.*;
import static de.morrien.voodoo.util.BindingUtil.*;

/**
 * Created by Phoebe Morrien
 */
public class PoppetItem extends Item {
    protected final Poppet.PoppetType poppetType;

    public PoppetItem(Poppet.PoppetType poppetType, Properties properties) {
        super(properties(poppetType, properties));
        this.poppetType = poppetType;
    }

    private static Properties properties(Poppet.PoppetType poppetType, Properties properties) {
        if (poppetType == BLANK)
            return properties.stacksTo(1);
        properties.durability(1);
        if (poppetType == VOODOO_PROTECTION || poppetType == REFLECTOR)
            properties.rarity(Rarity.RARE).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        else if (poppetType == DEATH_PROTECTION)
            properties.rarity(Rarity.UNCOMMON);
        return properties;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot slot) {
        if (!world.isClientSide()) {
            migrate(stack);
            if (world.getGameTime() % 100 == 0)
                checkForNameUpdate(stack, world);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        if (isBound(stack)) {
            final var text = Component.translatable(
                    "text.voodoo.poppet.bound",
                    getBoundName(stack)
            );
            text.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
            tooltip.accept(text);
        } else if (stack.getItem() != ItemRegistry.poppetMap.get(BLANK).get()) {
            final var text = Component.translatable("text.voodoo.poppet.not_bound");
            text.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
            tooltip.accept(text);
        }
        this.appendDisabledHoverText(stack, context, tooltip, flag);
    }

    protected void appendDisabledHoverText(ItemStack stack, TooltipContext context, Consumer<Component> tooltip, TooltipFlag flag) {
        final VoodooConfig.Common.PoppetBase config = poppetType.getConfig();
        if (config == null) return;
        if (!config.enabled.get()) {
            final var text = Component.translatable("text.voodoo.poppet.disabled");
            text.setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
            tooltip.accept(text);
        }
    }

    public Poppet.PoppetType getPoppetType() {
        return poppetType;
    }
}
