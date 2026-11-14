package de.morrien.voodoo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.morrien.voodoo.blockentity.PoppetShelfBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class PoppetShelfRenderer implements BlockEntityRenderer<PoppetShelfBlockEntity, PoppetShelfRenderer.State> {
    private final ItemModelResolver itemModelResolver;

    public PoppetShelfRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(PoppetShelfBlockEntity blockEntity, State state, float partialTicks, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.time = blockEntity.getLevel().getGameTime() + partialTicks;
        for (int i = 0; i < state.items.length; i++)
            itemModelResolver.updateForTopItem(state.items[i], blockEntity.getItem(i), ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);
    }

    @Override
    public void submit(State state, PoseStack matrixStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        for (int i = 0; i < state.items.length; i++) {
            ItemStackRenderState itemState = state.items[i];
            if (!itemState.isEmpty()) {
                matrixStack.pushPose();
                double offset = Math.sin(state.time / 8) / 32;
                //noinspection IntegerDivisionInFloatingPointContext
                matrixStack.translate((i % 3) / 5D + 0.3, 0.9 + offset, (i / 3) / 5D + 0.3);
                matrixStack.mulPose(Axis.YP.rotationDegrees(state.time));

                matrixStack.scale(0.4f, 0.4f, 0.4f);
                itemState.submit(matrixStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

                matrixStack.popPose();
            }
        }
    }

    public static class State extends BlockEntityRenderState {
        private final ItemStackRenderState[] items = new ItemStackRenderState[9];
        private float time;

        public State() {
            for (int i = 0; i < items.length; i++)
                items[i] = new ItemStackRenderState();
        }
    }
}
