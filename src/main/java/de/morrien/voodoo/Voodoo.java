package de.morrien.voodoo;

import de.morrien.voodoo.block.BlockRegistry;
import de.morrien.voodoo.container.ContainerRegistry;
import de.morrien.voodoo.datagen.RecipeGen;
import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.network.VoodooNetwork;
import de.morrien.voodoo.recipe.RecipeRegistry;
import de.morrien.voodoo.sound.SoundRegistry;
import de.morrien.voodoo.tileentity.PoppetShelfTileEntity;
import de.morrien.voodoo.tileentity.TileEntityTypeRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Voodoo.MOD_ID)
public class Voodoo {
    public static final String MOD_ID = "voodoo";
    public static final Logger logger = LogManager.getLogger(Voodoo.MOD_ID);

    public Voodoo() {
        // Registering event handlers
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::onGatherData);
        eventBus.addListener(this::clientSetup);

        // Setup VoodooNetwork
        VoodooNetwork.setup();

        // Register registries
        ItemRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BlockRegistry.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        RecipeRegistry.RECIPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        SoundRegistry.SOUND_EVENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ContainerRegistry.CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TileEntityTypeRegistry.TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());

        // Register configs
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, VoodooConfig.commonSpec);
    }

    private void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(new RecipeGen(generator));
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(TileEntityTypeRegistry.poppetShelfTileEntity.get(), PoppetShelfTileEntity.PoppetShelfRenderer::new);
    }
}
