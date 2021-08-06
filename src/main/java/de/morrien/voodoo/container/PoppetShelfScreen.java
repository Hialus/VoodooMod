package de.morrien.voodoo.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.morrien.voodoo.Voodoo;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PoppetShelfScreen extends AbstractContainerScreen<PoppetShelfContainer> {
    private ResourceLocation GUI = new ResourceLocation(Voodoo.MOD_ID, "textures/gui/poppet_shelf.png");

    public PoppetShelfScreen(PoppetShelfContainer container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    //@Override
    //protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
    //    String title;
    //    if (menu.poppetShelf.ownerName != null) {
    //        title = "";
    //    } else {
    //        title = I18n.get(BlockRegistry.poppetShelf.get().getDescriptionId());
    //    }
    //    drawString(matrixStack, Minecraft.getInstance().font, title, 8, 6, 0xffffff);
    //    drawString(matrixStack, Minecraft.getInstance().font, inventory.getDisplayName(), 8, imageHeight - 94, 0xffffff);
    //}

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }
}