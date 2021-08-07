package de.morrien.voodoo.network;

import de.morrien.voodoo.tileentity.PoppetShelfTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PoppetShelfSyncUpdate implements IThreadsafePacket {
    private final BlockPos pos;
    private final CompoundNBT inventoryTag;

    public PoppetShelfSyncUpdate(CompoundNBT inventoryTag, BlockPos pos) {
        this.inventoryTag = inventoryTag;
        this.pos = pos;
    }

    public PoppetShelfSyncUpdate(PacketBuffer buffer) {
        this.inventoryTag = buffer.readNbt();
        this.pos = buffer.readBlockPos();
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeNbt(this.inventoryTag);
        buffer.writeBlockPos(this.pos);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleThreadsafe(Context context) {
        World world = Minecraft.getInstance().level;
        if (world == null) return;
        TileEntity blockEntity = world.getBlockEntity(this.pos);
        if (blockEntity == null) return;
        if (blockEntity instanceof PoppetShelfTileEntity) {
            ((PoppetShelfTileEntity) blockEntity).updateInventory(this.inventoryTag);
            Minecraft.getInstance().levelRenderer.blockChanged(world, this.pos, null, null, 0);
        }
    }
}