package de.morrien.voodoo.network;

import de.morrien.voodoo.tileentity.PoppetShelfTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
    public void handleThreadsafe(Context context) {
        HandleClient.handle(this);
    }

    /**
     * Safely runs client side only code in a method only called on client
     */
    private static class HandleClient {
        private static void handle(PoppetShelfSyncUpdate packet) {
            World world = Minecraft.getInstance().level;

            if (world != null) {
                TileEntity te = world.getBlockEntity(packet.pos);

                if (te != null) {
                    if (te instanceof PoppetShelfTileEntity) {
                        ((PoppetShelfTileEntity) te).updateInventory(packet.inventoryTag);
                        Minecraft.getInstance().levelRenderer.blockChanged(world, packet.pos, null, null, 0);
                    }
                }
            }
        }
    }
}