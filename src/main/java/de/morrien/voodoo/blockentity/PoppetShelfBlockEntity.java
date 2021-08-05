package de.morrien.voodoo.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import de.morrien.voodoo.item.PoppetItem;
import de.morrien.voodoo.network.PoppetShelfSyncUpdate;
import de.morrien.voodoo.network.VoodooNetwork;
import de.morrien.voodoo.util.PoppetUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Created by Timor Morrien
 */
public class PoppetShelfBlockEntity extends BlockEntity {
    private UUID ownerUuid;
    private String ownerName;
    private boolean inventoryTouched;
    private PoppetShelfItemStackHandler itemHandler;
    private LazyOptional<IItemHandler> handler;

    public PoppetShelfBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityTypeRegistry.poppetShelfBlockEntity.get(), blockPos, blockState);
        this.itemHandler = new PoppetShelfItemStackHandler();
        this.handler = LazyOptional.of(() -> itemHandler);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, PoppetShelfBlockEntity poppetShelf) {
        if (!level.isClientSide && poppetShelf.inventoryTouched) {
            poppetShelf.inventoryTouched = false;
            poppetShelf.setChanged();

            if (level instanceof ServerLevel) {
                VoodooNetwork.getInstance().sendToClientsAround(new PoppetShelfSyncUpdate(poppetShelf.itemHandler.serializeNBT(), blockPos), level, blockPos);
            }
        }
    }

    public void inventoryTouched() {
        this.inventoryTouched = true;
        PoppetUtil.invalidateShelfCache(PoppetShelfBlockEntity.this);
    }

    public List<ItemStack> getInventory() {
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
    public void invalidateCaps() {
        super.invalidateCaps();
        handler.invalidate();
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(worldPosition, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        handleUpdateTag(pkt.getTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        final CompoundTag compound = super.getUpdateTag();
        this.saveToTag(compound);
        return compound;
    }

    @Override
    public void handleUpdateTag(CompoundTag compound) {
        super.handleUpdateTag(compound);
        this.readFromTag(compound);
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        super.save(compound);
        this.saveToTag(compound);
        return compound;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.readFromTag(compound);
    }

    private void saveToTag(CompoundTag compound) {
        compound.put("inv", itemHandler.serializeNBT());
        if (ownerUuid != null)
            compound.putUUID("owner_uuid", ownerUuid);
        if (ownerName != null)
            compound.putString("owner_name", ownerName);
    }

    private void readFromTag(CompoundTag compound) {
        itemHandler.deserializeNBT(compound.getCompound("inv"));
        PoppetUtil.removePoppetShelf(this.ownerUuid, this);
        if (compound.hasUUID("owner_uuid"))
            this.ownerUuid = compound.getUUID("owner_uuid");
        if (compound.contains("owner_name"))
            this.ownerName = compound.getString("owner_name");
        PoppetUtil.addPoppetShelf(this.ownerUuid, this);
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        PoppetUtil.removePoppetShelf(this.ownerUuid, this);
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public void setOwnerUuid(UUID ownerUuid) {
        if (this.ownerUuid != ownerUuid) {
            PoppetUtil.removePoppetShelf(this.ownerUuid, this);
            this.ownerUuid = ownerUuid;
            PoppetUtil.addPoppetShelf(this.ownerUuid, this);
            this.setChanged();
        }
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
        this.setChanged();
    }

    public void updateInventory(CompoundTag inventoryTag) {
        itemHandler.deserializeNBT(inventoryTag);
    }

    @OnlyIn(Dist.CLIENT)
    public static class PoppetShelfRenderer implements BlockEntityRenderer<PoppetShelfBlockEntity> {
        public PoppetShelfRenderer(BlockEntityRendererProvider.Context context) {
        }

        @Override
        public void render(PoppetShelfBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = blockEntity.itemHandler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    matrixStack.pushPose();
                    double offset = Math.sin((blockEntity.getLevel().getGameTime() + partialTicks) / 8) / 32;
                    //noinspection IntegerDivisionInFloatingPointContext
                    matrixStack.translate((i % 3) / 5D + 0.3, 0.9 + offset, (i / 3) / 5D + 0.3);
                    matrixStack.mulPose(Vector3f.YP.rotationDegrees(blockEntity.getLevel().getGameTime() + partialTicks * 2));

                    matrixStack.scale(0.4f, 0.4f, 0.4f);
                    Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer, 0);

                    matrixStack.popPose();
                }
            }
        }
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
            PoppetShelfBlockEntity.this.inventoryTouched();
        }

        private NonNullList<ItemStack> getInventory() {
            final NonNullList<ItemStack> inventory = NonNullList.create();
            inventory.addAll(stacks);
            return inventory;
        }
    }
}
