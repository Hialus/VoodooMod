package de.morrien.voodoo.neoforge;

import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.VoodooConfig;
import de.morrien.voodoo.blockentity.BlockEntityTypeRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;

@Mod(Voodoo.MOD_ID)
public class VoodooNeoForge {
    public VoodooNeoForge(IEventBus modBus, ModContainer container) {
        Voodoo.init();
        modBus.addListener(this::registerCapabilities);

        // Register configs
        container.registerConfig(ModConfig.Type.SERVER, VoodooConfig.commonSpec);
        container.registerConfig(ModConfig.Type.SERVER, VoodooConfig.durabilitySpec, Voodoo.MOD_ID + "-durability-server.toml");
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, BlockEntityTypeRegistry.poppetShelfBlockEntity.get(), (poppetShelf, side) -> VanillaContainerWrapper.of(poppetShelf));
    }
}
