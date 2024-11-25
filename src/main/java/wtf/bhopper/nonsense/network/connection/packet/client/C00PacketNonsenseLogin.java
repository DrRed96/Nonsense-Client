package wtf.bhopper.nonsense.network.connection.packet.client;

import net.minecraft.network.PacketBuffer;
import wtf.bhopper.nonsense.network.NonsenseNetHandler;
import wtf.bhopper.nonsense.network.connection.packet.NonsensePacket;

import java.io.IOException;

public class C00PacketNonsenseLogin implements NonsensePacket {

    private String token;

    public C00PacketNonsenseLogin() {}

    public C00PacketNonsenseLogin(String token) {
        this.token = token;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.token = buf.readStringFromBuffer(0x7FFF);
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeString(this.token);
    }

    @Override
    public void processPacket(NonsenseNetHandler handler) {

    }
}
