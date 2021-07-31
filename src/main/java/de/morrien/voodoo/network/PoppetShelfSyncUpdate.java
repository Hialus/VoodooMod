package de.morrien.voodoo.network;

import de.morrien.voodoo.tileentity.PoppetShelfTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PoppetShelfSyncUpdate implements IThreadsafePacket {

    private final BlockPos pos;
    private final NonNullList<ItemStack> itemStacks;

    public PoppetShelfSyncUpdate(NonNullList<ItemStack> itemStacks, BlockPos pos) {
        this.itemStacks = itemStacks;
        this.pos = pos;
    }

    public PoppetShelfSyncUpdate(PacketBuffer buffer) {
        int size = buffer.readInt();
        NonNullList<ItemStack> topStacks = NonNullList.withSize(size, ItemStack.EMPTY);

        for (int item = 0; item < size; item++) {
            ItemStack itemStack = buffer.readItemStack();

            topStacks.set(item, itemStack);
        }

        this.itemStacks = topStacks;

        this.pos = buffer.readBlockPos();
    }

    @Override
    public void encode(PacketBuffer packetBuffer) {
        packetBuffer.writeInt(this.itemStacks.size());

        for (ItemStack stack : this.itemStacks) {
            packetBuffer.writeItemStack(stack);
        }

        packetBuffer.writeBlockPos(this.pos);
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
            World world = Minecraft.getInstance().world;

            if (world != null) {
                TileEntity te = world.getTileEntity(packet.pos);

                if (te != null) {
                    if (te instanceof PoppetShelfTileEntity) {
                        ((PoppetShelfTileEntity) te).updateInventory(packet.itemStacks);
                        Minecraft.getInstance().worldRenderer.notifyBlockUpdate(world, packet.pos, null, null, 0);
                    }
                }
            }
        }
    }
}