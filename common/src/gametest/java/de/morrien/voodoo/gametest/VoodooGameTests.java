package de.morrien.voodoo.gametest;

import de.morrien.voodoo.Voodoo;
import net.minecraft.gametest.framework.AfterBatch;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.server.level.ServerLevel;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class VoodooGameTests {
    @GameTestGenerator
    public static Collection<TestFunction> generateTests() {
        return Stream.of(
                TaglockKitTests.tests(),
                BindPoppetRecipeTests.tests(),
                RepairTests.tests(),
                VoodooPoppetTests.tests(),
                VampiricPoppetTests.tests(),
                ProtectionPoppetTests.tests(),
                ReflectorPoppetTests.tests(),
                PoppetShelfTests.tests(),
                CommandTests.tests()
        ).flatMap(List::stream).toList();
    }

    @AfterBatch(batch = Voodoo.MOD_ID)
    public static void afterBatch(ServerLevel level) {
        TestUtil.removePlayers(level);
    }
}
