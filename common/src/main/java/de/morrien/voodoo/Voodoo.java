package de.morrien.voodoo;

import de.morrien.voodoo.block.BlockRegistry;
import de.morrien.voodoo.blockentity.BlockEntityTypeRegistry;
import de.morrien.voodoo.component.VoodooComponents;
import de.morrien.voodoo.container.ContainerRegistry;
import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.recipe.RecipeRegistry;
import de.morrien.voodoo.sound.SoundRegistry;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Voodoo {
    public static final String MOD_ID = "voodoo";
    public static final Logger logger = LoggerFactory.getLogger(Voodoo.MOD_ID);

    public static void init() {
        // Register registries
        SoundRegistry.SOUND_EVENTS.register();
        VoodooComponents.COMPONENTS.register();
        BlockRegistry.BLOCKS.register();
        ItemRegistry.ITEMS.register();
        BlockEntityTypeRegistry.BLOCK_ENTITIES.register();
        ContainerRegistry.CONTAINERS.register();
        RecipeRegistry.RECIPES.register();
        VoodooGroup.CREATIVE_MODE_TABS.register();
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
