package de.morrien.voodoo.blockentity;

import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.block.BlockRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityTypeRegistry {
    public static final BlockEntityType<PoppetShelfBlockEntity> poppetShelfBlockEntity = FabricBlockEntityTypeBuilder.create(PoppetShelfBlockEntity::new, BlockRegistry.poppetShelf).build(null);

    public static void register() {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(Voodoo.MOD_ID, "poppet_shelf_block_entity"), poppetShelfBlockEntity);
    }
}