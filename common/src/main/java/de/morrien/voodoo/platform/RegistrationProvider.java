package de.morrien.voodoo.platform;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public interface RegistrationProvider<T> {
    static <T> RegistrationProvider<T> get(ResourceKey<? extends Registry<T>> registryKey) {
        return Services.PLATFORM.createRegistrationProvider(registryKey);
    }

    <I extends T> Supplier<I> register(String name, Function<ResourceKey<T>, ? extends I> factory);

    Collection<? extends Supplier<? extends T>> getEntries();

    void register();
}
