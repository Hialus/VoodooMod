package de.morrien.voodoo.tileentity;

import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.block.BlockRegistry;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityTypeRegistry {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Voodoo.MOD_ID);
    public static final RegistryObject<TileEntityType<PoppetShelfTileEntity>> poppetShelfTileEntity = TILE_ENTITIES.register("poppet_shelf_tile_entity", () -> TileEntityType.Builder.create(PoppetShelfTileEntity::new, BlockRegistry.poppetShelf.get()).build(null));
}