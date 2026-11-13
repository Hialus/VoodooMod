package de.morrien.voodoo.neoforge.item;

import de.morrien.voodoo.item.VampiricPoppetItem;
import net.minecraft.world.item.ItemStack;

public class NeoVampiricPoppetItem extends VampiricPoppetItem {
    public NeoVampiricPoppetItem(Properties properties) {
        super(properties);
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
