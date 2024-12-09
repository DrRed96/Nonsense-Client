package net.minecraft.network;

import net.minecraft.util.IThreadListener;

public class PacketThreadUtil
{
    public static <T extends INetHandler> void checkThreadAndEnqueue(final Packet<T> packet, final T netHandler, IThreadListener listener) throws ThreadQuickExitException
    {
        if (!listener.isCallingFromMinecraftThread())
        {
            listener.addScheduledTask(() -> packet.processPacket(netHandler));
            throw ThreadQuickExitException.INSTANCE;
        }
    }
}
