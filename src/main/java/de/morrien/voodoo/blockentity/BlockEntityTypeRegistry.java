package de.morrien.voodoo.blockentity;

import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.block.BlockRegistry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockEntityTypeRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Voodoo.MOD_ID);
    public static final RegistryObject<BlockEntityType<PoppetShelfBlockEntity>> poppetShelfBlockEntity = BLOCK_ENTITIES.register("poppet_shelf_block_entity", () -> BlockEntityType.Builder.of(PoppetShelfBlockEntity::new, BlockRegistry.poppetShelf.get()).build(null));
}