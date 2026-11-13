package de.morrien.voodoo.neoforge.event;

import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.entity.PoppetEntityHandler;
import de.morrien.voodoo.event.VoodooEvents;
import de.morrien.voodoo.item.PoppetItem;
import de.morrien.voodoo.util.PoppetShelfChunkLoader;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Voodoo.MOD_ID)
public class NeoForgeEvents {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        VoodooEvents.onRegisterCommands(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player)
            VoodooEvents.onTickPlayerTick(player);
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.EntityInteract event) {
        VoodooEvents.onPlayerInteract(event.getEntity(), event.getItemStack(), event.getTarget());
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (VoodooEvents.onLivingDeath(event.getEntity(), event.getSource()))
            event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        final float remaining = VoodooEvents.onLivingAttack(event.getEntity(), event.getSource(), event.getAmount());
        if (remaining == event.getAmount()) return;
        if (remaining <= 0)
            event.setCanceled(true);
        else
            event.setAmount(remaining);
    }

    @SubscribeEvent
    public static void onEntityInvulnerabilityCheck(EntityInvulnerabilityCheckEvent event) {
        if (event.getEntity() instanceof ItemEntity itemEntity && PoppetEntityHandler.onFire(itemEntity, event.getSource()))
            event.setInvulnerable(true);
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level)
            PoppetShelfChunkLoader.onLevelLoad(level);
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (event.getLeft().getItem() instanceof PoppetItem && event.getRight().is(event.getLeft().getItem())) {
            if (event.getPlayer().containerMenu instanceof AnvilMenu menu)
                menu.getSlot(menu.getResultSlot()).set(ItemStack.EMPTY);
            event.setCanceled(true);
        }
    }
}
