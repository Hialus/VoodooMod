package de.morrien.voodoo.fabric.platform;

import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.platform.RegistrationProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class FabricRegistrationProvider<T> implements RegistrationProvider<T> {
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final List<Entry<T, ? extends T>> entries = new ArrayList<>();

    public FabricRegistrationProvider(ResourceKey<? extends Registry<T>> registryKey) {
        this.registryKey = registryKey;
    }

    @Override
    public <I extends T> Supplier<I> register(String name, Function<ResourceKey<T>, ? extends I> factory) {
        final Entry<T, I> entry = new Entry<>(ResourceKey.create(registryKey, Voodoo.id(name)), factory);
        entries.add(entry);
        return entry;
    }

    @Override
    public Collection<? extends Supplier<? extends T>> getEntries() {
        return entries;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register() {
        final Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.getValue(registryKey.location());
        for (Entry<T, ? extends T> entry : entries) {
            entry.register(registry);
        }
    }

    private static class Entry<T, I extends T> implements Supplier<I> {
        private final ResourceKey<T> key;
        private final Function<ResourceKey<T>, ? extends I> factory;
        private I value;

        private Entry(ResourceKey<T> key, Function<ResourceKey<T>, ? extends I> factory) {
            this.key = key;
            this.factory = factory;
        }

        private void register(Registry<T> registry) {
            value = Registry.register(registry, key, factory.apply(key));
        }

        @Override
        public I get() {
            return value;
        }
    }
}
