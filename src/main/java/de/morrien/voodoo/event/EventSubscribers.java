package de.morrien.voodoo.event;

import de.morrien.voodoo.event.wrapper.LivingDeathEvent;
import de.morrien.voodoo.event.wrapper.PlayerTickEvent;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;

import static de.morrien.voodoo.event.VoodooEvents.onLivingDeath;
import static de.morrien.voodoo.event.VoodooEvents.onTickPlayerTick;

public class EventSubscribers {

    public static void serverTickEventSubscriber() {
        ServerTickEvents.START_SERVER_TICK.register((server) -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                onTickPlayerTick(new PlayerTickEvent(player));
            }
        });
    }

    public static void playerDeathEventSubscriber() {
        ServerPlayerEvents.ALLOW_DEATH.register((player, damageSource, damageAmount) -> {
            var event = new LivingDeathEvent(player, damageSource);
            onLivingDeath(event);
            return !event.isCanceled();
        });
    }

    public static void commandRegistrationSubscriber() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            VoodooEvents.onRegisterCommands(dispatcher);
        });
    }
}
