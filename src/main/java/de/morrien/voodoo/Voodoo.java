package de.morrien.voodoo;

import de.morrien.voodoo.block.BlockRegistry;
import de.morrien.voodoo.blockentity.BlockEntityTypeRegistry;
import de.morrien.voodoo.container.ContainerRegistry;
import de.morrien.voodoo.event.EventSubscribers;
import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.recipe.RecipeRegistry;
import de.morrien.voodoo.sound.SoundRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Voodoo Mod Main Class
 *
 * Created by Timor Morrien
 */
public class Voodoo implements ModInitializer {
    public static final String MOD_ID = "voodoo";
    public static final Logger logger = LoggerFactory.getLogger(Voodoo.MOD_ID);

    @Override
    public void onInitialize() {
        // Register configs
        ModLoadingContext.registerConfig(MOD_ID, ModConfig.Type.COMMON, VoodooConfig.commonSpec);
        // Make sure the creative tab is registered
        var groupInstance = VoodooGroup.INSTANCE;

        ItemRegistry.register();
        BlockRegistry.register();
        BlockEntityTypeRegistry.register();
        ContainerRegistry.register();
        RecipeRegistry.register();
        SoundRegistry.register();
        EventSubscribers.serverTickEventSubscriber();
        EventSubscribers.playerDeathEventSubscriber();
        EventSubscribers.commandRegistrationSubscriber();
    }
}
