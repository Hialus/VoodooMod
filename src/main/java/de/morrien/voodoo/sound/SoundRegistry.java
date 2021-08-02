package de.morrien.voodoo.sound;

import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.block.BlockRegistry;
import de.morrien.voodoo.tileentity.PoppetShelfTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Voodoo.MOD_ID);
    public static final RegistryObject<SoundEvent> voodooProtectionPoppetUsed = SOUND_EVENTS.register("poppet.voodoo_protection.used", () -> new SoundEvent(new ResourceLocation(Voodoo.MOD_ID, "poppet.voodoo_protection.used")));
}
