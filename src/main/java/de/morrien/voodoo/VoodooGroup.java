package de.morrien.voodoo;

import de.morrien.voodoo.item.ItemRegistry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class VoodooGroup extends CreativeModeTab {
    public static final VoodooGroup INSTANCE = new VoodooGroup();

    public VoodooGroup() {
        super("voodoo_group");
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(ItemRegistry.poppetMap.get(Poppet.PoppetType.BLANK).get());
    }
}