package de.morrien.voodoo.client;

import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.container.PoppetShelfContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PoppetShelfScreen extends AbstractContainerScreen<PoppetShelfContainer> {
    private ResourceLocation GUI = Voodoo.id("textures/gui/poppet_shelf.png");

    public PoppetShelfScreen(PoppetShelfContainer container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
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
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(RenderType::guiTextured, GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}