package de.morrien.voodoo.network;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

/**
 * Packet instance that automatically wraps the logic in {@link NetworkEvent.Context#enqueueWork(Runnable)} for thread safety
 */
public interface IThreadsafePacket extends ISimplePacket {
    @Override
    @OnlyIn(Dist.CLIENT)
    default void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> handleThreadsafe(context));
        context.setPacketHandled(true);
    }

    /**
     * Handles receiving the packet on the correct thread
     * Packet is automatically set to handled as well by the base logic
     *
     * @param context Packet context
     */
    @OnlyIn(Dist.CLIENT)
    void handleThreadsafe(NetworkEvent.Context context);
}