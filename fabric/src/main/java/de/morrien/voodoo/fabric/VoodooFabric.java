package de.morrien.voodoo.fabric;

import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.VoodooConfig;
import de.morrien.voodoo.blockentity.BlockEntityTypeRegistry;
import de.morrien.voodoo.fabric.event.FabricEvents;
import de.morrien.voodoo.item.PoppetItem;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.component.DataComponents;
import net.neoforged.fml.config.ModConfig;

public class VoodooFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Voodoo.init();
        FabricEvents.register();
        ItemStorage.SIDED.registerForBlockEntity(ContainerStorage::of, BlockEntityTypeRegistry.poppetShelfBlockEntity.get());
        DefaultItemComponentEvents.MODIFY.register(context -> context.modify(
                item -> item instanceof PoppetItem poppetItem && poppetItem.getPoppetType().hasDurability(),
                (builder, item) -> builder.set(DataComponents.MAX_DAMAGE, ((PoppetItem) item).getPoppetType().getDurability())
        ));

        // Register configs
        ConfigRegistry.INSTANCE.register(Voodoo.MOD_ID, ModConfig.Type.SERVER, VoodooConfig.commonSpec);
        ConfigRegistry.INSTANCE.register(Voodoo.MOD_ID, ModConfig.Type.STARTUP, VoodooConfig.durabilitySpec, Voodoo.MOD_ID + "-durability-startup.toml");
    }
}
