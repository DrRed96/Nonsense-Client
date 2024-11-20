package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.impl.exploit.Disabler;

public class MessageSerializer extends MessageToByteEncoder<Packet> {
    private static final Logger logger = LogManager.getLogger();
    private static final Marker RECEIVED_PACKET_MARKER = MarkerManager.getMarker("PACKET_SENT", NetworkManager.logMarkerPackets);
    private final EnumPacketDirection direction;

    public MessageSerializer(EnumPacketDirection direction) {
        this.direction = direction;
    }

    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {

        EnumConnectionState connectionState = ctx.channel().attr(NetworkManager.attrKeyConnectionState).get();
        Integer id = connectionState.getPacketId(this.direction, packet);

        if (logger.isDebugEnabled()) {
            logger.debug(RECEIVED_PACKET_MARKER, "OUT: [{}:{}] {}", new Object[]{
                    ctx.channel().attr(NetworkManager.attrKeyConnectionState).get(),
                    id, packet.getClass().getName()
            });
        }

        if (id == null) {
            throw new IOException("Can't serialize unregistered packet");
        }

        PacketBuffer buffer = new PacketBuffer(out);
        buffer.writeVarIntToBuffer(id);

        try {
            packet.writePacketData(buffer);

            if (Nonsense.module(Disabler.class).packetFuck()) {
                if (this.direction == EnumPacketDirection.SERVERBOUND && connectionState == EnumConnectionState.PLAY) {
                    buffer.writeByte(0x0F);
                    buffer.writeByte(0x03);
                    buffer.writeByte(0x05);
                    buffer.writeByte(0x06);
                }
            }

        } catch (Throwable throwable) {
            logger.error(throwable);

        }
    }
}
