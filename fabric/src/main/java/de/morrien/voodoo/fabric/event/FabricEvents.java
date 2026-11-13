package de.morrien.voodoo.fabric.event;

import de.morrien.voodoo.blockentity.PoppetShelfBlockEntity;
import de.morrien.voodoo.event.VoodooEvents;
import de.morrien.voodoo.util.PoppetShelfChunkLoader;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;

public class FabricEvents {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> VoodooEvents.onRegisterCommands(dispatcher));

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                VoodooEvents.onTickPlayerTick(player);
            }
        });

        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            VoodooEvents.onPlayerInteract(player, player.getItemInHand(hand), entity);
            return InteractionResult.PASS;
        });

        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> !VoodooEvents.onLivingDeath(entity, source));

        ServerWorldEvents.LOAD.register((server, level) -> PoppetShelfChunkLoader.onLevelLoad(level));

        ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, level) -> {
            if (blockEntity instanceof PoppetShelfBlockEntity poppetShelf)
                level.getServer().schedule(level.getServer().wrapRunnable(poppetShelf::onLoad));
        });
    }
}
