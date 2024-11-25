package wtf.bhopper.nonsense.network.connection.packet;

import net.minecraft.network.PacketBuffer;
import wtf.bhopper.nonsense.network.NonsenseNetHandler;

import java.io.IOException;

public interface NonsensePacket {

    void readPacketData(PacketBuffer buf) throws IOException;
    void writePacketData(PacketBuffer buf) throws IOException;
    void processPacket(NonsenseNetHandler handler);

}
