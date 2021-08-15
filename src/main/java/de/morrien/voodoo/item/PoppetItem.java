package de.morrien.voodoo.item;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.VoodooConfig;
import de.morrien.voodoo.VoodooGroup;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

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
                .durability(poppetType.getDurability())
                .setNoRepair()
        );
        this.poppetType = poppetType;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        if (isBound(stack)) {
            checkForNameUpdate(stack, world);
            final TranslationTextComponent text = new TranslationTextComponent(
                    "text.voodoo.poppet.bound",
                    getBoundName(stack)
            );
            text.setStyle(Style.EMPTY.withColor(TextFormatting.GRAY));
            tooltip.add(text);
        } else if (stack.getItem() != ItemRegistry.poppetMap.get(BLANK).get()) {
            final TranslationTextComponent text = new TranslationTextComponent("text.voodoo.poppet.not_bound");
            text.setStyle(Style.EMPTY.withColor(TextFormatting.GRAY));
            tooltip.add(text);
        }
        this.appendDisabledHoverText(stack, world, tooltip, flag);
    }

    protected void appendDisabledHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        final VoodooConfig.Common.PoppetBase config = poppetType.getConfig();
        if (config == null) return;
        if (!config.enabled.get()) {
            final TranslationTextComponent text = new TranslationTextComponent("text.voodoo.poppet.disabled");
            text.setStyle(Style.EMPTY.withColor(TextFormatting.RED));
            tooltip.add(text);
        }
    }

    @Override
    public boolean canBeDepleted() {
        return poppetType.hasDurability();
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return poppetType.getDurability();
    }

    public Poppet.PoppetType getPoppetType() {
        return poppetType;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
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
