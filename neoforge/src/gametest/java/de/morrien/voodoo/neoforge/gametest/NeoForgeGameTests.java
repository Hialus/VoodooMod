package de.morrien.voodoo.neoforge.gametest;

import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.gametest.VoodooGameTests;
import net.neoforged.fml.common.Mod;

@Mod(Voodoo.MOD_ID)
public class NeoForgeGameTests {
    public NeoForgeGameTests() {
        VoodooGameTests.init();
    }
}
