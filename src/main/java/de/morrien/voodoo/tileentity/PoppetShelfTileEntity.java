package de.morrien.voodoo.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.item.PoppetItem;
import de.morrien.voodoo.network.PoppetShelfSyncUpdate;
import de.morrien.voodoo.network.VoodooNetwork;
import de.morrien.voodoo.util.PoppetUtil;
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
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Timor Morrien
 */
public class PoppetShelfTileEntity extends TileEntity implements ITickableTileEntity {
    private UUID ownerUuid;
    private String ownerName;
    private boolean inventoryTouched;
    private PoppetShelfItemStackHandler itemHandler;
    private LazyOptional<IItemHandler> handler;

    public PoppetShelfTileEntity() {
        super(TileEntityTypeRegistry.poppetShelfTileEntity.get());
        PoppetUtil.invalidateShelvesCache();
        this.itemHandler = new PoppetShelfItemStackHandler();
        this.handler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.inventoryTouched) {
            this.inventoryTouched = false;
            this.setChanged();

            if (this.level instanceof ServerWorld) {
                VoodooNetwork.getInstance().sendToClientsAround(new PoppetShelfSyncUpdate(itemHandler.serializeNBT(), this.worldPosition), (ServerWorld) this.level, this.worldPosition);
            }
        }
    }

    public Poppet getPoppet(Poppet.PoppetType type) {
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = itemHandler.getStackInSlot(i);
            if (itemStack.isEmpty()) continue;
            Item item = itemStack.getItem();
            if (item instanceof PoppetItem) {
                PoppetItem poppetItem = (PoppetItem) item;
                if (poppetItem.getPoppetType() == type) {
                    this.inventoryTouched = true;
                    return new Poppet(this, poppetItem, itemStack);
                }
            }
        }
        return null;
    }

    public List<ItemStack> getInventory() {
        this.inventoryTouched = true;
        return itemHandler.getInventory();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return handler.cast();
        else
            return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        handler.invalidate();
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(level.getBlockState(pkt.getPos()), pkt.getTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        final CompoundNBT compound = super.getUpdateTag();
        this.saveToTag(compound);
        return compound;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT compound) {
        super.handleUpdateTag(state, compound);
        this.readFromTag(compound);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        this.saveToTag(compound);
        return compound;
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        this.readFromTag(compound);
    }

    private void saveToTag(CompoundNBT compound) {
        compound.put("inv", itemHandler.serializeNBT());
        if (ownerUuid != null)
            compound.putUUID("owner_uuid", ownerUuid);
        if (ownerName != null)
            compound.putString("owner_name", ownerName);
    }

    private void readFromTag(CompoundNBT compound) {
        itemHandler.deserializeNBT(compound.getCompound("inv"));
        if (compound.hasUUID("owner_uuid"))
            this.ownerUuid = compound.getUUID("owner_uuid");
        if (compound.contains("owner_name"))
            this.ownerName = compound.getString("owner_name");
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public void setOwnerUuid(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
        this.setChanged();
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
        this.setChanged();
    }

    public void updateInventory(CompoundNBT inventoryTag) {
        itemHandler.deserializeNBT(inventoryTag);
    }

    public class PoppetShelfItemStackHandler extends ItemStackHandler {
        public PoppetShelfItemStackHandler() {
            super(9);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.getItem() instanceof PoppetItem;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            PoppetShelfTileEntity.this.inventoryTouched = true;
            PoppetUtil.invalidateShelfCache(PoppetShelfTileEntity.this);
        }

        private NonNullList<ItemStack> getInventory() {
            final NonNullList<ItemStack> inventory = NonNullList.create();
            inventory.addAll(stacks);
            return inventory;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class PoppetShelfRenderer extends TileEntityRenderer<PoppetShelfTileEntity> {
        public PoppetShelfRenderer(TileEntityRendererDispatcher rendererDispatcher) {
            super(rendererDispatcher);
        }

        @Override
        public void render(PoppetShelfTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = tileEntity.itemHandler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    matrixStack.pushPose();
                    double offset = Math.sin((tileEntity.getLevel().getGameTime() + partialTicks) / 8) / 32;
                    //noinspection IntegerDivisionInFloatingPointContext
                    matrixStack.translate((i % 3) / 5D + 0.3, 0.9 + offset, (i / 3) / 5D + 0.3);
                    matrixStack.mulPose(Vector3f.YP.rotationDegrees(tileEntity.getLevel().getGameTime() + partialTicks * 2));

                    matrixStack.scale(0.4f, 0.4f, 0.4f);
                    Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer);

                    matrixStack.popPose();
                }
            }
        }
    }
}
