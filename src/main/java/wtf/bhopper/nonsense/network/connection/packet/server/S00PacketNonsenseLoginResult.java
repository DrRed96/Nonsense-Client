package wtf.bhopper.nonsense.network.connection.packet.server;

import net.minecraft.network.PacketBuffer;
import wtf.bhopper.nonsense.network.Account;
import wtf.bhopper.nonsense.network.NonsenseNetHandler;
import wtf.bhopper.nonsense.network.connection.packet.NonsensePacket;

import java.io.IOException;

public class S00PacketNonsenseLoginResult implements NonsensePacket {

    private Account account;
    private byte[] privateKey;
    private byte[] publicKey;

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {

    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {

    }

    @Override
    public void processPacket(NonsenseNetHandler handler) {
        handler.handleLoginResult(this);
    }
}
