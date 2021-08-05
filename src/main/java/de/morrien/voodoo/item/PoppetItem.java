package de.morrien.voodoo.item;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.VoodooDamageSource;
import de.morrien.voodoo.VoodooGroup;
import de.morrien.voodoo.entity.PoppetItemEntity;
import de.morrien.voodoo.util.PoppetUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

import static de.morrien.voodoo.Poppet.PoppetType.*;
import static de.morrien.voodoo.VoodooConfig.COMMON;
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

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        final CompoundNBT tag = stack.getOrCreateTag();
        if ((poppetType == VOODOO_PROTECTION || poppetType == REFLECTOR) && !tag.contains("Enchantments")) {
            stack.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 10);
            tag.putInt("HideFlags", 1);
        }
        return null;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return poppetType == DEATH_PROTECTION ? Rarity.UNCOMMON : super.getRarity(stack);
    }
}
