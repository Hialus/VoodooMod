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
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        drawString(matrixStack, Minecraft.getInstance().fontRenderer, I18n.format(BlockRegistry.poppetShelf.get().getTranslationKey()), 8, 6, 0xffffff);
        drawString(matrixStack, Minecraft.getInstance().fontRenderer, playerInventory.getDisplayName(), 8, ySize - 94, 0xffffff);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.xSize, this.ySize);
    }
}