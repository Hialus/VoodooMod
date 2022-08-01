package de.morrien.voodoo.item;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.VoodooConfig;
import de.morrien.voodoo.VoodooGroup;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import static de.morrien.voodoo.Poppet.PoppetType.*;
import static de.morrien.voodoo.util.BindingUtil.*;

/**
 * Created by Timor Morrien
 */
public class PoppetItem extends Item {
    protected final Poppet.PoppetType poppetType;

    public PoppetItem(Poppet.PoppetType poppetType) {
        super(new Properties()
                .tab(VoodooGroup.INSTANCE)
                .durability(1)
        );
        this.poppetType = poppetType;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        if (isBound(stack)) {
            checkForNameUpdate(stack, world);
            final var text = Component.translatable(
                    "text.voodoo.poppet.bound",
                    getBoundName(stack)
            );
            text.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
            tooltip.add(text);
        } else if (stack.getItem() != ItemRegistry.poppetMap.get(BLANK)) {
            final var text = Component.translatable("text.voodoo.poppet.not_bound");
            text.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
            tooltip.add(text);
        }
        this.appendDisabledHoverText(stack, world, tooltip, flag);
    }

    protected void appendDisabledHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        final VoodooConfig.Common.PoppetBase config = poppetType.getConfig();
        if (config == null) return;
        if (!config.enabled.get()) {
            final var text = Component.translatable("text.voodoo.poppet.disabled");
            text.setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
            tooltip.add(text);
        }
    }

    @Override
    public boolean canBeDepleted() {
        return poppetType.hasDurability();
    }

    @Override
    public int getMaxDamage() {
        return poppetType.getDurability();
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        return Math.round(13.0F - (float) itemStack.getDamageValue() * 13.0F / (float) this.getMaxDamage());
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        float f = Math.max(0.0F, ((float) this.getMaxDamage() - (float) itemStack.getDamageValue()) / (float) this.getMaxDamage());
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    public Poppet.PoppetType getPoppetType() {
        return poppetType;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return poppetType == VOODOO_PROTECTION || poppetType == REFLECTOR;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return isFoil(stack) ? Rarity.RARE : poppetType == DEATH_PROTECTION ? Rarity.UNCOMMON : super.getRarity(stack);
    }
}
