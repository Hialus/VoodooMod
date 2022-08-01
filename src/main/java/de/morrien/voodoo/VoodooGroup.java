package de.morrien.voodoo;

import de.morrien.voodoo.item.ItemRegistry;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class VoodooGroup {
    public static final CreativeModeTab INSTANCE = FabricItemGroupBuilder.build(
            new ResourceLocation(Voodoo.MOD_ID, "voodoo_group"),
            () -> new ItemStack(ItemRegistry.poppetMap.get(Poppet.PoppetType.BLANK))
    );
}