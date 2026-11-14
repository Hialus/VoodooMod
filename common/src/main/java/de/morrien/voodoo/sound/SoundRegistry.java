package de.morrien.voodoo.sound;

import de.morrien.voodoo.platform.RegistrationProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class SoundRegistry {
    public static final RegistrationProvider<SoundEvent> SOUND_EVENTS = RegistrationProvider.get(Registries.SOUND_EVENT);
    public static final Supplier<SoundEvent> voodooProtectionPoppetUsed = SOUND_EVENTS.register("poppet.voodoo_protection.used", key -> SoundEvent.createVariableRangeEvent(key.identifier()));
}
