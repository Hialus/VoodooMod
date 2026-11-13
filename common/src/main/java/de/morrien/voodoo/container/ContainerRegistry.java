package de.morrien.voodoo.container;

import de.morrien.voodoo.platform.RegistrationProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class ContainerRegistry {
    public static final RegistrationProvider<MenuType<?>> CONTAINERS = RegistrationProvider.get(Registries.MENU);
    public static final Supplier<MenuType<PoppetShelfContainer>> poppetShelf = CONTAINERS.register("poppet_shelf", key -> new MenuType<>(PoppetShelfContainer::new, FeatureFlags.VANILLA_SET));
}
