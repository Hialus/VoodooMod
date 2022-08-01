package de.morrien.voodoo.block;

import de.morrien.voodoo.Voodoo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class BlockRegistry {
    public static final Block poppetShelf = new PoppetShelfBlock();

    public static void register() {
        Registry.register(Registry.BLOCK, new ResourceLocation(Voodoo.MOD_ID, "poppet_shelf"), poppetShelf);
    }
}
