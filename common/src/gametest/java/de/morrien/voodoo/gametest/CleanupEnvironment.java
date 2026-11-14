package de.morrien.voodoo.gametest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;

/**
 * Removes the test players after each batch and restores the changed config values.
 */
public record CleanupEnvironment(boolean restoreConfig) implements TestEnvironmentDefinition<Unit> {
    public static final MapCodec<CleanupEnvironment> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("restore_config", false).forGetter(CleanupEnvironment::restoreConfig)
    ).apply(instance, CleanupEnvironment::new));

    @Override
    public Unit setup(ServerLevel level) {
        return Unit.INSTANCE;
    }

    @Override
    public void teardown(ServerLevel level, Unit saveData) {
        if (restoreConfig)
            TestUtil.restoreConfig();
        TestUtil.removePlayers();
    }

    @Override
    public MapCodec<CleanupEnvironment> codec() {
        return CODEC;
    }
}
