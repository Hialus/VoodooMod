package de.morrien.voodoo.neoforge.platform;

import de.morrien.voodoo.platform.RegistrationProvider;
import de.morrien.voodoo.platform.services.IPlatformHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

public class NeoForgePlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.getCurrent().isProduction();
    }

    @Override
    public <T> RegistrationProvider<T> createRegistrationProvider(ResourceKey<? extends Registry<T>> registryKey) {
        return new NeoForgeRegistrationProvider<>(registryKey);
    }
}
