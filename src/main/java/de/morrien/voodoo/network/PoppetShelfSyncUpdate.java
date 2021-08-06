package de.morrien.voodoo.network;

import de.morrien.voodoo.blockentity.PoppetShelfBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class PoppetShelfSyncUpdate implements IThreadsafePacket {
    private final BlockPos pos;
    private final CompoundTag inventoryTag;

    public PoppetShelfSyncUpdate(CompoundTag inventoryTag, BlockPos pos) {
        this.inventoryTag = inventoryTag;
        this.pos = pos;
    }

    public PoppetShelfSyncUpdate(FriendlyByteBuf buffer) {
        this.inventoryTag = buffer.readNbt();
        this.pos = buffer.readBlockPos();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeNbt(this.inventoryTag);
        buffer.writeBlockPos(this.pos);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleThreadsafe(NetworkEvent.Context context) {
        Level world = Minecraft.getInstance().level;

        if (world == null) return;
        BlockEntity blockEntity = world.getBlockEntity(this.pos);
        if (blockEntity == null) return;
        if (blockEntity instanceof PoppetShelfBlockEntity) {
            ((PoppetShelfBlockEntity) blockEntity).updateInventory(this.inventoryTag);
            Minecraft.getInstance().levelRenderer.blockChanged(world, this.pos, null, null, 0);
        }
    }
}