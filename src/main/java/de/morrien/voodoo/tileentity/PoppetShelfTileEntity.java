package de.morrien.voodoo.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.item.PoppetItem;
import de.morrien.voodoo.network.PoppetShelfSyncUpdate;
import de.morrien.voodoo.network.VoodooNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Created by Timor Morrien
 */
public class PoppetShelfTileEntity extends TileEntity implements IInventory, ITickableTileEntity {
    public UUID owner;
    private boolean inventoryTouched;
    private NonNullList<ItemStack> inventory = NonNullList.withSize(9, ItemStack.EMPTY);
    private net.minecraftforge.common.util.LazyOptional<?> itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> createUnSidedHandler());

    public PoppetShelfTileEntity() {
        super(TileEntityTypeRegistry.poppetShelfTileEntity.get());
    }

    @Override
    public void tick() {
        if (!this.world.isRemote && this.inventoryTouched) {
            this.inventoryTouched = false;

            if (this.world instanceof ServerWorld) {
                VoodooNetwork.getInstance().sendToClientsAround(new PoppetShelfSyncUpdate(inventory, this.pos), (ServerWorld) this.world, this.pos);
            }
        }
    }

    public Poppet getPoppet(Poppet.PoppetType type) {
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = getStackInSlot(i);
            if (itemStack.isEmpty()) continue;
            Item item = itemStack.getItem();
            if (item instanceof PoppetItem) {
                PoppetItem poppetItem = (PoppetItem) item;
                if (poppetItem.getPoppetType() == type) {
                    return new Poppet(this, poppetItem, itemStack);
                }
            }
        }
        return null;
    }

    public List<ItemStack> getInventory() {
        final NonNullList<ItemStack> inventory = NonNullList.create();
        inventory.addAll(this.inventory);
        return inventory;
    }

    protected net.minecraftforge.items.IItemHandler createUnSidedHandler() {
        return new net.minecraftforge.items.wrapper.InvWrapper(this);
    }

    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, @javax.annotation.Nullable net.minecraft.util.Direction side) {
        if (!this.removed && cap == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return itemHandler.cast();
        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(world.getBlockState(pkt.getPos()), pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        final CompoundNBT compound = super.getUpdateTag();
        compound.put("inv", ItemStackHelper.saveAllItems(new CompoundNBT(), this.inventory));
        if (owner != null)
            compound.putUniqueId("owner", owner);
        return compound;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT compound) {
        super.handleUpdateTag(state, compound);
        this.inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound.getCompound("inv"), this.inventory);
        if (compound.hasUniqueId("owner"))
            this.owner = compound.getUniqueId("owner");
    }

    @Override
    public int getSizeInventory() {
        return 9;
    }

    @Override
    public boolean isEmpty() {
        return inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        this.inventoryTouched = true;
        return inventory.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemstack = ItemStackHelper.getAndSplit(inventory, index, count);
        if (!itemstack.isEmpty()) {
            this.markDirty();
        }
        this.inventoryTouched = true;

        return itemstack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        this.inventoryTouched = true;
        return ItemStackHelper.getAndRemove(inventory, index);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return stack.getItem() instanceof PoppetItem;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory.set(index, stack);
        this.inventoryTouched = true;
        this.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        if (this.world.getTileEntity(this.pos) != this) {
            return false;
        } else {
            return !(player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) > 64.0D);
        }
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        this.inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound.getCompound("inv"), this.inventory);
        if (compound.hasUniqueId("owner"))
            this.owner = compound.getUniqueId("owner");
    }

    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.put("inv", ItemStackHelper.saveAllItems(new CompoundNBT(), this.inventory));
        if (owner != null)
            compound.putUniqueId("owner", owner);
        return compound;
    }

    public void updateInventory(NonNullList<ItemStack> itemStacks) {
        this.inventory = itemStacks;
    }

    @OnlyIn(Dist.CLIENT)
    public static class PoppetShelfRenderer extends TileEntityRenderer<PoppetShelfTileEntity> {
        public PoppetShelfRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
            super(rendererDispatcherIn);
        }

        @Override
        public void render(PoppetShelfTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = tileEntity.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    matrixStack.push();
                    //RenderSystem.enableRescaleNormal();
                    //RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1f);
                    //RenderSystem.enableBlend();
                    //RenderHelper.enableStandardItemLighting();
                    //RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

                    double offset = Math.sin((tileEntity.getWorld().getGameTime() + partialTicks) / 8) / 32;
                    //noinspection IntegerDivisionInFloatingPointContext
                    matrixStack.translate((i % 3) / 5D + 0.3, 0.9 + offset, (i / 3) / 5D + 0.3);
                    matrixStack.rotate(Vector3f.YP.rotationDegrees(tileEntity.getWorld().getGameTime() + partialTicks * 2));
                    //matrixStack.rotate(new Quaternion(tileEntity.getWorld().getGameTime() + partialTicks * 2, 0, 1, 0));

                    matrixStack.scale(0.4f, 0.4f, 0.4f);
                    //Minecraft.getInstance().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                    Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer);

                    //RenderSystem.disableRescaleNormal();
                    //RenderSystem.disableBlend();
                    matrixStack.pop();
                }
            }
        }
    }
}
