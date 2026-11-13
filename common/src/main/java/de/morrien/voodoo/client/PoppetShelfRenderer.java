package de.morrien.voodoo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.morrien.voodoo.blockentity.PoppetShelfBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PoppetShelfRenderer implements BlockEntityRenderer<PoppetShelfBlockEntity> {
    public PoppetShelfRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(PoppetShelfBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = blockEntity.getItem(i);
            if (!stack.isEmpty()) {
                matrixStack.pushPose();
                double offset = Math.sin((blockEntity.getLevel().getGameTime() + partialTicks) / 8) / 32;
                //noinspection IntegerDivisionInFloatingPointContext
                matrixStack.translate((i % 3) / 5D + 0.3, 0.9 + offset, (i / 3) / 5D + 0.3);
                matrixStack.mulPose(Axis.YP.rotationDegrees(blockEntity.getLevel().getGameTime() + partialTicks * 2));

                matrixStack.scale(0.4f, 0.4f, 0.4f);
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, combinedLight, combinedOverlay, matrixStack, buffer, blockEntity.getLevel(), 0);

                matrixStack.popPose();
            }
        }
    }
}
