package wtf.bhopper.nonsense.network.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.PacketBuffer;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.network.connection.packet.NonsensePacket;
import wtf.bhopper.nonsense.network.connection.packet.NonsensePacketRegistry;

import java.io.IOException;

public class NonsenseEncoder extends MessageToByteEncoder<NonsensePacket> {
    @Override
    protected void encode(ChannelHandlerContext ctx, NonsensePacket packet, ByteBuf out) throws Exception {
        Integer id = NonsensePacketRegistry.PACKETS.getPacketId(EnumPacketDirection.SERVERBOUND, packet);

        if (id == null) {
            throw new IOException("Can't serialize unregistered packet");
        }

        PacketBuffer buffer = new PacketBuffer(out);
        buffer.writeVarIntToBuffer(id);

        try {
            packet.writePacketData(buffer);
        } catch (Throwable throwable) {
            Nonsense.LOGGER.error(throwable);
        }

    }
}
