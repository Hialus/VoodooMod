package de.morrien.voodoo.neoforge.gametest;

import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.gametest.VoodooGameTests;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

@EventBusSubscriber(modid = Voodoo.MOD_ID)
public class NeoForgeGameTests {
    @SubscribeEvent
    public static void onRegisterGameTests(RegisterGameTestsEvent event) {
        event.register(VoodooGameTests.class);
    }
}
