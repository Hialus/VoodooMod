package de.morrien.voodoo.component;

import de.morrien.voodoo.platform.RegistrationProvider;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

import java.util.function.Supplier;

public class VoodooComponents {
    public static final RegistrationProvider<DataComponentType<?>> COMPONENTS = RegistrationProvider.get(Registries.DATA_COMPONENT_TYPE);
    public static final Supplier<DataComponentType<Binding>> binding = COMPONENTS.register("binding", key -> DataComponentType.<Binding>builder()
            .persistent(Binding.CODEC)
            .networkSynchronized(Binding.STREAM_CODEC)
            .build()
    );
}
