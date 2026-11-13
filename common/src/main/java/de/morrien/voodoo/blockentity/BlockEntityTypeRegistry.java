package de.morrien.voodoo.blockentity;

import de.morrien.voodoo.block.BlockRegistry;
import de.morrien.voodoo.platform.RegistrationProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;
import java.util.function.Supplier;

public class BlockEntityTypeRegistry {
    public static final RegistrationProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistrationProvider.get(Registries.BLOCK_ENTITY_TYPE);
    public static final Supplier<BlockEntityType<PoppetShelfBlockEntity>> poppetShelfBlockEntity = BLOCK_ENTITIES.register("poppet_shelf_block_entity", key -> new BlockEntityType<>(PoppetShelfBlockEntity::new, Set.of(BlockRegistry.poppetShelf.get())));
}
