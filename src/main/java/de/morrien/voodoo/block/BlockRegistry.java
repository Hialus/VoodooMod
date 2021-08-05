package de.morrien.voodoo.block;

import de.morrien.voodoo.Voodoo;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Voodoo.MOD_ID);
    public static final RegistryObject<Block> poppetShelf = BLOCKS.register("poppet_shelf", PoppetShelfBlock::new);
}
