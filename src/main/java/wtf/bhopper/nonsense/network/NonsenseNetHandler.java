package wtf.bhopper.nonsense.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.network.connection.NonsenseConnection;
import wtf.bhopper.nonsense.network.connection.packet.server.S00PacketNonsenseLoginResult;

public class NonsenseNetHandler {

    private static final Logger LOGGER = LogManager.getLogger(Nonsense.NAME);

    private final NonsenseConnection connection;
    private Account account;

    public NonsenseNetHandler(NonsenseConnection connection) {
        this.connection = connection;
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
