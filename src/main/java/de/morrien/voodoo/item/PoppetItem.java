package de.morrien.voodoo.item;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.VoodooGroup;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import static de.morrien.voodoo.Poppet.PoppetType.BLANK;
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
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        if (isBound(stack)) {
            checkForNameUpdate(stack, world);
            final TranslatableComponent text = new TranslatableComponent(
                    "text.voodoo.poppet.bound",
                    getBoundName(stack)
            );
            text.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
            tooltip.add(text);
        } else if (stack.getItem() != ItemRegistry.poppetMap.get(BLANK).get()) {
            final TranslatableComponent text = new TranslatableComponent("text.voodoo.poppet.not_bound");
            text.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
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
}
