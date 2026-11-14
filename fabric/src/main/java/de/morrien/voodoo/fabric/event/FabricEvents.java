package de.morrien.voodoo.fabric.event;

import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.VoodooConfig;
import de.morrien.voodoo.blockentity.PoppetShelfBlockEntity;
import de.morrien.voodoo.event.VoodooEvents;
import de.morrien.voodoo.util.PoppetShelfChunkLoader;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;

public class FabricEvents {
    private static MinecraftServer server;

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

        ServerLevelEvents.LOAD.register((server, level) -> PoppetShelfChunkLoader.onLevelLoad(level));
        ServerChunkEvents.CHUNK_LOAD.register((level, chunk, generated) -> PoppetShelfChunkLoader.onChunkLoad(level, chunk));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> PoppetShelfChunkLoader.onPlayerJoin(handler.getPlayer()));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> PoppetShelfChunkLoader.onPlayerLeave(handler.getPlayer()));
        ServerLifecycleEvents.SERVER_STARTING.register(server -> FabricEvents.server = server);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> FabricEvents.server = null);
        ModConfigEvents.reloading(Voodoo.MOD_ID).register(config -> {
            final MinecraftServer server = FabricEvents.server;
            if (config.getSpec() == VoodooConfig.commonSpec && server != null)
                server.execute(() -> PoppetShelfChunkLoader.onConfigReload(server));
        });

        ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, level) -> {
            if (blockEntity instanceof PoppetShelfBlockEntity poppetShelf)
                level.getServer().schedule(level.getServer().wrapRunnable(poppetShelf::onLoad));
        });
    }
}
