package de.morrien.voodoo.container;

import de.morrien.voodoo.Voodoo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public class ContainerRegistry {
    public static final MenuType<PoppetShelfContainer> poppetShelf = new MenuType<>(PoppetShelfContainer::new);

    public static void register() {
        Registry.register(Registry.MENU, new ResourceLocation(Voodoo.MOD_ID, "poppet_shelf"), poppetShelf);
    }
}
