package de.morrien.voodoo.client;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.util.BindingUtil;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;

public class VoodooItemProperties {
    public static void register() {
        ItemProperties.register(
                ItemRegistry.taglockKit.get(),
                Voodoo.id("filled"),
                (itemStack, level, livingEntity, seed) -> BindingUtil.isBound(itemStack) ? 1 : 0
        );
        ItemProperties.register(
                ItemRegistry.poppetMap.get(Poppet.PoppetType.PROJECTILE_PROTECTION).get(),
                Voodoo.id("percentage_used"),
                (itemStack, level, livingEntity, seed) -> Math.min(1, Math.max(0, itemStack.getDamageValue() / (float) itemStack.getMaxDamage()))
        );
    }
}
