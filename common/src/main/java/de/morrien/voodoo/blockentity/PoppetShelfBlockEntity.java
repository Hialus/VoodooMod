package de.morrien.voodoo.blockentity;

import com.mojang.serialization.Dynamic;
import de.morrien.voodoo.item.PoppetItem;
import de.morrien.voodoo.util.BindingUtil;
import de.morrien.voodoo.util.PoppetShelfChunkLoader;
import de.morrien.voodoo.util.PoppetUtil;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Created by Phoebe Morrien
 */
public class PoppetShelfBlockEntity extends BlockEntity implements Container {
    // Data version of Minecraft 1.19.1 for converting inventories of poppet shelves from older version to new ones
    private static final int LEGACY_DATA_VERSION = 3117;
    private UUID ownerUuid;
    private String ownerName;
    private boolean inventoryTouched;
    private final NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);

    public PoppetShelfBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityTypeRegistry.poppetShelfBlockEntity.get(), blockPos, blockState);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, PoppetShelfBlockEntity poppetShelf) {
        if (!level.isClientSide && poppetShelf.inventoryTouched) {
            poppetShelf.inventoryTouched = false;
            setChanged(level, blockPos, blockState);
            level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_CLIENTS);
        }
    }

    /**
     * Called when the poppet shelf was added in a level, whether by placing it or loading the chunk it is in.
     * Registers poppet shelves for chunk loading that are not registered yet, e.g. from older versions.
     */
    public void onLoad() {
        if (this.level instanceof ServerLevel serverLevel && !this.isRemoved() && this.ownerUuid != null)
            PoppetShelfChunkLoader.setShelfOwner(serverLevel, this.worldPosition, this.ownerUuid);
    }

    public void inventoryTouched() {
        this.inventoryTouched = true;
        PoppetUtil.invalidateShelfCache(PoppetShelfBlockEntity.this);
    }

    public NonNullList<ItemStack> getInventory() {
        return items;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        final ItemStack stack = ContainerHelper.removeItem(items, slot, amount);
        if (!stack.isEmpty())
            this.setChanged();
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        stack.limitSize(this.getMaxStackSize(stack));
        this.setChanged();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return stack.getItem() instanceof PoppetItem;
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
        this.setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.inventoryTouched();
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
        ContainerHelper.saveAllItems(compound, items, registries);
        if (ownerUuid != null)
            compound.putUUID("owner_uuid", ownerUuid);
        if (ownerName != null)
            compound.putString("owner_name", ownerName);
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.loadAdditional(compound, registries);
        items.clear();
        if (compound.contains("inv"))
            this.loadLegacyItems(compound.getCompound("inv"), registries);
        else
            ContainerHelper.loadAllItems(compound, items, registries);
        PoppetUtil.removePoppetShelf(this.ownerUuid, this);
        if (compound.hasUUID("owner_uuid"))
            this.ownerUuid = compound.getUUID("owner_uuid");
        if (compound.contains("owner_name"))
            this.ownerName = compound.getString("owner_name");
        PoppetUtil.addPoppetShelf(this.ownerUuid, this);
        PoppetUtil.invalidateShelfCache(this);
    }

    /**
     * Reads the inventory of a poppet shelf from older versions, which was stored in an ItemStackHandler.
     * The item stacks are run through a data fixer, as Minecraft cannot convert them otherwise.
     *
     * @param inventory  The old inventory tag
     * @param registries The registry lookup
     */
    private void loadLegacyItems(CompoundTag inventory, HolderLookup.Provider registries) {
        final ListTag list = inventory.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            final CompoundTag itemTag = list.getCompound(i);
            final int slot = itemTag.getInt("Slot");
            if (slot < 0 || slot >= items.size()) continue;
            final Tag updated = DataFixers.getDataFixer().update(
                    References.ITEM_STACK,
                    new Dynamic<>(NbtOps.INSTANCE, itemTag),
                    LEGACY_DATA_VERSION,
                    SharedConstants.getCurrentVersion().getDataVersion().getVersion()
            ).getValue();
            ItemStack.parse(registries, updated).ifPresent(stack -> {
                BindingUtil.migrate(stack);
                items.set(slot, stack);
            });
        }
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
}
