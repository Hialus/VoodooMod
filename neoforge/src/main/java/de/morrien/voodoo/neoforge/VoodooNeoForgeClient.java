package de.morrien.voodoo.neoforge;

import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.blockentity.BlockEntityTypeRegistry;
import de.morrien.voodoo.client.PoppetShelfRenderer;
import de.morrien.voodoo.client.PoppetShelfScreen;
import de.morrien.voodoo.client.VoodooItemProperties;
import de.morrien.voodoo.container.ContainerRegistry;
import de.morrien.voodoo.neoforge.datagen.RecipeGen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod(value = Voodoo.MOD_ID, dist = Dist.CLIENT)
public class VoodooNeoForgeClient {
    public VoodooNeoForgeClient(IEventBus modBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modBus.addListener(this::registerMenuScreens);
        modBus.addListener(this::registerRenderers);
        modBus.addListener(this::onGatherData);
        modBus.addListener(this::onClientSetup);
    }

    private void onGatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeServer(), new RecipeGen(event.getGenerator().getPackOutput(), event.getLookupProvider()));
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(VoodooItemProperties::register);
    }

    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ContainerRegistry.poppetShelf.get(), PoppetShelfScreen::new);
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityTypeRegistry.poppetShelfBlockEntity.get(), PoppetShelfRenderer::new);
    }
}
