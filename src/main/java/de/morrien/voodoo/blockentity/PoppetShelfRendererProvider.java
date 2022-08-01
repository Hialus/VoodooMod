package de.morrien.voodoo.blockentity;

import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;

public class PoppetShelfRendererProvider implements BlockEntityRendererProvider<PoppetShelfBlockEntity> {
    @Override
    public BlockEntityRenderer<PoppetShelfBlockEntity> create(BlockEntityRendererProvider.Context context) {
        return (blockEntity, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay) -> {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = blockEntity.getItem(i);
                if (!stack.isEmpty()) {
                    matrixStack.pushPose();
                    double offset = Math.sin((blockEntity.getLevel().getGameTime() + partialTicks) / 8) / 32;
                    matrixStack.translate((i % 3) / 5D + 0.3, 0.9 + offset, (i / 3) / 5D + 0.3);
                    matrixStack.mulPose(Vector3f.YP.rotationDegrees(blockEntity.getLevel().getGameTime() + partialTicks * 2));

                    matrixStack.scale(0.4f, 0.4f, 0.4f);
                    Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer, 0);

                    matrixStack.popPose();
                }
            }
        };
    }
}
