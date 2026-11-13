package de.morrien.voodoo.platform.services;

import de.morrien.voodoo.platform.RegistrationProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface IPlatformHelper {
    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    <T> RegistrationProvider<T> createRegistrationProvider(ResourceKey<? extends Registry<T>> registryKey);
}
