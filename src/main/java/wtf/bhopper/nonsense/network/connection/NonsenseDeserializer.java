package wtf.bhopper.nonsense.network.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.PacketBuffer;
import wtf.bhopper.nonsense.network.connection.packet.NonsensePacket;
import wtf.bhopper.nonsense.network.connection.packet.NonsensePacketRegistry;

import java.io.IOException;
import java.util.List;

public class NonsenseDeserializer extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() != 0) {
            PacketBuffer buffer = new PacketBuffer(in);
            int id = buffer.readVarIntFromBuffer();
            NonsensePacket packet = NonsensePacketRegistry.PACKETS.getPacket(EnumPacketDirection.CLIENTBOUND, id);

            if (packet == null) {
                throw new IOException("Bad packet ID " + id);
            }

            packet.readPacketData(buffer);

            if (buffer.readableBytes() > 0) {
                throw new IOException("Packet " + id + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + buffer.readableBytes() + " bytes extra whilst reading packet " + id);
            }

            out.add(packet);
        }
    }
}
