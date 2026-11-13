package de.morrien.voodoo.neoforge.platform;

import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.platform.RegistrationProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public class NeoForgeRegistrationProvider<T> implements RegistrationProvider<T> {
    private final DeferredRegister<T> register;

    public NeoForgeRegistrationProvider(ResourceKey<? extends Registry<T>> registryKey) {
        this.register = DeferredRegister.create(registryKey, Voodoo.MOD_ID);
    }

    @Override
    public <I extends T> Supplier<I> register(String name, Function<ResourceKey<T>, ? extends I> factory) {
        return register.register(name, id -> factory.apply(ResourceKey.create(register.getRegistryKey(), id)));
    }

    @Override
    public Collection<? extends Supplier<? extends T>> getEntries() {
        return register.getEntries();
    }

    @Override
    public void register() {
        register.register(ModLoadingContext.get().getActiveContainer().getEventBus());
    }
}
