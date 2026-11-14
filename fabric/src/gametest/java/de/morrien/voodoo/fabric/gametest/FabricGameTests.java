package de.morrien.voodoo.fabric.gametest;

import de.morrien.voodoo.gametest.VoodooGameTests;
import net.fabricmc.api.ModInitializer;

public class FabricGameTests implements ModInitializer {
    @Override
    public void onInitialize() {
        VoodooGameTests.init();
    }
}
