package de.morrien.voodoo;

import de.morrien.voodoo.item.ItemRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class VoodooGroup extends ItemGroup {
    public static final VoodooGroup INSTANCE = new VoodooGroup();

    public VoodooGroup() {
        super("voodoo_group");
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(ItemRegistry.needle.get());
    }
}