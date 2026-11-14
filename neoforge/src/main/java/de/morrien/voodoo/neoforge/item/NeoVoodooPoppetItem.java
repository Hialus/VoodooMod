package de.morrien.voodoo.neoforge.item;

import de.morrien.voodoo.entity.PoppetEntityHandler;
import de.morrien.voodoo.item.VoodooPoppetItem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class NeoVoodooPoppetItem extends VoodooPoppetItem {
    public NeoVoodooPoppetItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return Math.max(1, poppetType.getDurability());
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        PoppetEntityHandler.onTick(entity);
        return false;
    }
}
