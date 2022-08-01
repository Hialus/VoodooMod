package de.morrien.voodoo.event;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.util.BindingUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class ClientEvents {

    public static void propertyOverrideRegistry() {
        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
            ItemProperties.register(
                    ItemRegistry.taglockKit,
                    new ResourceLocation(Voodoo.MOD_ID, "filled"),
                    (itemStack, clientWorld, livingEntity, var4) -> BindingUtil.isBound(itemStack) ? 1 : 0
            );
            ItemProperties.register(
                    ItemRegistry.poppetMap.get(Poppet.PoppetType.PROJECTILE_PROTECTION),
                    new ResourceLocation(Voodoo.MOD_ID, "percentage_used"),
                    (itemStack, clientWorld, livingEntity, var4) -> Math.min(1, Math.max(0, itemStack.getDamageValue() / (float) itemStack.getMaxDamage()))
            );
        });
    }
}