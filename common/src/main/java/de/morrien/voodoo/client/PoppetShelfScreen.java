package de.morrien.voodoo.client;

import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.container.PoppetShelfContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class PoppetShelfScreen extends AbstractContainerScreen<PoppetShelfContainer> {
    private Identifier GUI = Voodoo.id("textures/gui/poppet_shelf.png");

    public PoppetShelfScreen(PoppetShelfContainer container, Inventory inv, Component name) {
        super(container, inv, name);
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
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTicks);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}