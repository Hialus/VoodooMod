package de.morrien.voodoo.gametest;

import com.mojang.serialization.MapCodec;
import de.morrien.voodoo.platform.RegistrationProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Registers the test functions and the test environment type.
 */
public class VoodooGameTests {
    private static final RegistrationProvider<Consumer<GameTestHelper>> TEST_FUNCTIONS = RegistrationProvider.get(Registries.TEST_FUNCTION);
    private static final RegistrationProvider<MapCodec<? extends TestEnvironmentDefinition<?>>> TEST_ENVIRONMENT_TYPES = RegistrationProvider.get(Registries.TEST_ENVIRONMENT_DEFINITION_TYPE);

    public static void init() {
        Stream.of(
                TaglockKitTests.tests(),
                BindPoppetRecipeTests.tests(),
                RepairTests.tests(),
                VoodooPoppetTests.tests(),
                VampiricPoppetTests.tests(),
                ProtectionPoppetTests.tests(),
                ReflectorPoppetTests.tests(),
                PoppetShelfTests.tests(),
                ChunkLoadingTests.tests(),
                CommandTests.tests()
        ).flatMap(List::stream).forEach(test -> TEST_FUNCTIONS.register(test.name(), key -> test.function()));
        TEST_ENVIRONMENT_TYPES.register("cleanup", key -> CleanupEnvironment.CODEC);
        TEST_FUNCTIONS.register();
        TEST_ENVIRONMENT_TYPES.register();
    }
}
