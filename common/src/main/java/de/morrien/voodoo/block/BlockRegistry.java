package de.morrien.voodoo.block;

import de.morrien.voodoo.platform.RegistrationProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

public class BlockRegistry {
    public static final RegistrationProvider<Block> BLOCKS = RegistrationProvider.get(Registries.BLOCK);
    public static final Supplier<Block> poppetShelf = BLOCKS.register("poppet_shelf", key -> new PoppetShelfBlock(BlockBehaviour.Properties
            .of()
            .mapColor(MapColor.NETHER)
            .strength(6, 6)
            .requiresCorrectToolForDrops()
            .sound(SoundType.NETHER_BRICKS)
            .noOcclusion()
    ));
}
