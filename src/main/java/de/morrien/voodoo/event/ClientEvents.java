package de.morrien.voodoo.event;

import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.VoodooUtil;
import de.morrien.voodoo.container.ContainerRegistry;
import de.morrien.voodoo.container.PoppetShelfScreen;
import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.item.TaglockKitItem;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void propertyOverrideRegistry(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ScreenManager.register(ContainerRegistry.poppetShelf.get(), PoppetShelfScreen::new);
            ItemModelsProperties.register(
                    ItemRegistry.taglockKit.get(),
                    new ResourceLocation(Voodoo.MOD_ID, "filled"),
                    (itemStack, clientWorld, livingEntity) -> VoodooUtil.isBound(itemStack) ? 1 : 0
            );
        });
    }
}