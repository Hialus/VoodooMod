package de.morrien.voodoo.neoforge.item;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.item.PoppetItem;
import net.minecraft.world.item.ItemStack;

public class NeoPoppetItem extends PoppetItem {
    public NeoPoppetItem(Poppet.PoppetType poppetType, Properties properties) {
        super(poppetType, properties);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return Math.max(1, poppetType.getDurability());
    }

    @Override
    public boolean isCombineRepairable(ItemStack stack) {
        return false;
    }
}
