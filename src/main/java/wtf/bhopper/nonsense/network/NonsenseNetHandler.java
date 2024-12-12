package wtf.bhopper.nonsense.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.network.connection.NonsenseConnection;
import wtf.bhopper.nonsense.network.connection.packet.client.C00PacketNonsenseLogin;
import wtf.bhopper.nonsense.network.connection.packet.server.S00PacketNonsenseLoginResult;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class NonsenseNetHandler {

    private static final Logger LOGGER = LogManager.getLogger(Nonsense.NAME);

    private NonsenseConnection connection;
    private Account account;

    public NonsenseNetHandler() {

    }

    public void connect() {
        try {
            this.connection = NonsenseConnection.connect(InetAddress.getByName("localhost"), 6969, this);
            this.connection.sendPacket(new C00PacketNonsenseLogin("123"));
        } catch (IOException exception) {
            LOGGER.error("Failed to connect", exception);
        }
    }

    public void handleLoginResult(S00PacketNonsenseLoginResult packet) {

    }

    public NonsenseConnection getConnection() {
        return this.connection;
    }

    public Account getAccount() {
        return this.account;
    }

}
