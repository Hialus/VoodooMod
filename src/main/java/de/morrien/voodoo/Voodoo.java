package de.morrien.voodoo;

import de.morrien.voodoo.block.BlockRegistry;
import de.morrien.voodoo.command.VoodooCommand;
import de.morrien.voodoo.container.ContainerRegistry;
import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.network.VoodooNetwork;
import de.morrien.voodoo.recipe.RecipeRegistry;
import de.morrien.voodoo.tileentity.PoppetShelfTileEntity;
import de.morrien.voodoo.tileentity.TileEntityTypeRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Voodoo.MOD_ID)
public class Voodoo {
    public static final String MOD_ID = "voodoo";
    public static final Logger logger = LogManager.getLogger(Voodoo.MOD_ID);

    public Voodoo() {
        // Registering event handlers
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        // Setup VoodooNetwork
        VoodooNetwork.setup();

        // Register registries
        ItemRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BlockRegistry.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        RecipeRegistry.RECIPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        TileEntityTypeRegistry.TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ContainerRegistry.CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());

        // Register configs
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, VoodooConfig.commonSpec);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(TileEntityTypeRegistry.poppetShelfTileEntity.get(), PoppetShelfTileEntity.PoppetShelfRenderer::new);
    }
}
