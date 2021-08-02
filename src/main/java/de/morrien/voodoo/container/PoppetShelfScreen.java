package de.morrien.voodoo.container;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.block.BlockRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class PoppetShelfScreen extends ContainerScreen<PoppetShelfContainer> {
    private ResourceLocation GUI = new ResourceLocation(Voodoo.MOD_ID, "textures/gui/poppet_shelf.png");

    public PoppetShelfScreen(PoppetShelfContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
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
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }
}