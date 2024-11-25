package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class MessageDeserializer extends ByteToMessageDecoder
{
    private static final Logger logger = LogManager.getLogger();
    private static final Marker RECEIVED_PACKET_MARKER = MarkerManager.getMarker("PACKET_RECEIVED", NetworkManager.logMarkerPackets);
    private final EnumPacketDirection direction;

    public MessageDeserializer(EnumPacketDirection direction)
    {
        this.direction = direction;
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
    {
        if (in.readableBytes() != 0)
        {
            PacketBuffer buffer = new PacketBuffer(in);
            int id = buffer.readVarIntFromBuffer();
            Packet packet = ctx.channel().attr(NetworkManager.attrKeyConnectionState).get().getPacket(this.direction, id);

            if (packet == null)
            {
                throw new IOException("Bad packet id " + id);
            }
            else
            {
                packet.readPacketData(buffer);

                if (buffer.readableBytes() > 0)
                {
                    throw new IOException("Packet " + ctx.channel().attr(NetworkManager.attrKeyConnectionState).get().getId() + "/" + id + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + buffer.readableBytes() + " bytes extra whilst reading packet " + id);
                }
                else
                {
                    out.add(packet);

                    if (logger.isDebugEnabled())
                    {
                        logger.debug(RECEIVED_PACKET_MARKER, " IN: [{}:{}] {}", new Object[] {ctx.channel().attr(NetworkManager.attrKeyConnectionState).get(), Integer.valueOf(id), packet.getClass().getName()});
                    }
                }
            }
        }
    }
}
