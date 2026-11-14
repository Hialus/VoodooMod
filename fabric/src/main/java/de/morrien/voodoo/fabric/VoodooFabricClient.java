package de.morrien.voodoo.fabric;

import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.blockentity.BlockEntityTypeRegistry;
import de.morrien.voodoo.client.PoppetShelfRenderer;
import de.morrien.voodoo.client.PoppetShelfScreen;
import de.morrien.voodoo.client.VoodooItemProperties;
import de.morrien.voodoo.container.ContainerRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

public class VoodooFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ConfigScreenFactoryRegistry.INSTANCE.register(Voodoo.MOD_ID, ConfigurationScreen::new);
        MenuScreens.register(ContainerRegistry.poppetShelf.get(), PoppetShelfScreen::new);
        BlockEntityRenderers.register(BlockEntityTypeRegistry.poppetShelfBlockEntity.get(), PoppetShelfRenderer::new);
        VoodooItemProperties.register();
    }
}
