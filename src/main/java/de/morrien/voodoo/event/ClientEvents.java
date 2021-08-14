package de.morrien.voodoo.event;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.container.ContainerRegistry;
import de.morrien.voodoo.container.PoppetShelfScreen;
import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.util.BindingUtil;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void propertyOverrideRegistry(FMLClientSetupEvent event) {
        ScreenManager.register(ContainerRegistry.poppetShelf.get(), PoppetShelfScreen::new);
        ItemRegistry.taglockKit.get().addProperty(
                new ResourceLocation(Voodoo.MOD_ID, "filled"),
                (itemStack, clientWorld, livingEntity) -> BindingUtil.isBound(itemStack) ? 1 : 0
        );
        ItemRegistry.poppetMap.get(Poppet.PoppetType.PROJECTILE_PROTECTION).get().addProperty(
                new ResourceLocation(Voodoo.MOD_ID, "percentage_used"),
                (itemStack, clientWorld, livingEntity) -> Math.min(1, Math.max(0, itemStack.getDamageValue() / (float) itemStack.getMaxDamage()))
        );
    }
}