package de.morrien.voodoo;

import de.morrien.voodoo.blockentity.BlockEntityTypeRegistry;
import de.morrien.voodoo.blockentity.PoppetShelfBlockEntity;
import de.morrien.voodoo.blockentity.PoppetShelfRendererProvider;
import de.morrien.voodoo.container.ContainerRegistry;
import de.morrien.voodoo.container.PoppetShelfScreen;
import de.morrien.voodoo.event.ClientEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Voodoo Mod Client Main Class
 *
 * Created by Timor Morrien
 */
public class VoodooClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientEvents.propertyOverrideRegistry();
        MenuScreens.register(ContainerRegistry.poppetShelf, PoppetShelfScreen::new);
        BlockEntityRendererRegistry.register(BlockEntityTypeRegistry.poppetShelfBlockEntity, new PoppetShelfRendererProvider());

        ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation(Voodoo.MOD_ID, "update"), (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            NonNullList<ItemStack> inv = NonNullList.withSize(9, ItemStack.EMPTY);
            for (int i = 0; i < 9; i++) {
                inv.set(i, buf.readItem());
            }
            client.execute(() -> {
                PoppetShelfBlockEntity blockEntity = (PoppetShelfBlockEntity) Minecraft.getInstance().level.getBlockEntity(pos);
                blockEntity.setInventory(inv);
            });
        });
    }
}
