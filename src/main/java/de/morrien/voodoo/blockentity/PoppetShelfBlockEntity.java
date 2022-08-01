package de.morrien.voodoo.blockentity;

import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.container.ImplementedInventory;
import de.morrien.voodoo.util.PoppetUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.UUID;

/**
 * Created by Timor Morrien
 */
public class PoppetShelfBlockEntity extends BlockEntity implements Nameable, ImplementedInventory {
    private UUID ownerUuid;
    private String ownerName;
    private boolean inventoryTouched;
    private NonNullList<ItemStack> inventory = NonNullList.withSize(9, ItemStack.EMPTY);
    public boolean firstTick;

    public PoppetShelfBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypeRegistry.poppetShelfBlockEntity, pos, state);
        firstTick = true;
    }

    public static void tick(Level world, BlockPos pos, BlockState state, PoppetShelfBlockEntity entity) {
        if (world.isClientSide()) return;
        if (entity.inventoryTouched) {
            entity.setChanged();
            Collection<ServerPlayer> viewers = PlayerLookup.tracking(entity);
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeBlockPos(pos);
            for (ItemStack stack : entity.getInventory()) {
                buf.writeItem(stack);
            }
            entity.inventoryTouched = false;
            viewers.forEach(player -> ServerPlayNetworking.send(player, new ResourceLocation(Voodoo.MOD_ID, "update"), buf));
        }
        if (entity.firstTick && entity.isPlayerNearby(world, pos)) {
            entity.inventoryTouched = true;
            entity.firstTick = false;
        }
    }

    private boolean isPlayerNearby(Level world, BlockPos pos) {
        return world.hasNearbyAlivePlayer((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, 16.0);
    }

    public void inventoryTouched() {
        this.inventoryTouched = true;
        PoppetUtil.invalidateShelfCache(PoppetShelfBlockEntity.this);
    }

    public void setInventory(NonNullList<ItemStack> list) {
        this.inventory = list;
        this.inventoryTouched = true;
    }

    @Override
    public NonNullList<ItemStack> getInventory() {
        this.inventoryTouched();
        return inventory;
    }

    @Override
    public Component getName() {
        MutableComponent component;
        if (this.getOwnerName() == null) {
            component = Component.translatable("text.voodoo.poppet.not_bound");
        } else {
            final Player player = level.getPlayerByUUID(this.getOwnerUuid());
            if (player != null && !this.getOwnerName().equals(player.getName().getString()))
                this.setOwnerName(player.getName().getString());
            component = Component.literal(this.getOwnerName());
        }
        return Component.translatable("screen.voodoo.poppet_shelf", component);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
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

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        this.readFromTag(compoundTag);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        this.saveToTag(compoundTag);
    }

    private void saveToTag(CompoundTag compound) {
        compound.put("inv", ContainerHelper.saveAllItems(new CompoundTag(), inventory));
        if (ownerUuid != null)
            compound.putUUID("owner_uuid", ownerUuid);
        if (ownerName != null)
            compound.putString("owner_name", ownerName);
    }

    private void readFromTag(CompoundTag compound) {
        ContainerHelper.loadAllItems(compound.getCompound("inv"), inventory);
        PoppetUtil.removePoppetShelf(this.ownerUuid, this);
        if (compound.hasUUID("owner_uuid"))
            this.ownerUuid = compound.getUUID("owner_uuid");
        if (compound.contains("owner_name"))
            this.ownerName = compound.getString("owner_name");
        PoppetUtil.addPoppetShelf(this.ownerUuid, this);
    }
}
