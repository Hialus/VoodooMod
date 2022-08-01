package de.morrien.voodoo.sound;

import de.morrien.voodoo.Voodoo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundRegistry {
    public static final SoundEvent voodooProtectionPoppetUsed = new SoundEvent(new ResourceLocation(Voodoo.MOD_ID, "poppet.voodoo_protection.used"));

    public static void register() {
        Registry.register(Registry.SOUND_EVENT, new ResourceLocation(Voodoo.MOD_ID, "poppet.voodoo_protection.used"), voodooProtectionPoppetUsed);
    }
}
