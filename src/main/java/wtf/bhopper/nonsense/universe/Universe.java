package wtf.bhopper.nonsense.universe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.universe.packet.PacketHandler;
import wtf.bhopper.nonsense.universe.packet.api.AbstractClientPacket;
import wtf.bhopper.nonsense.util.misc.Http;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Universe {

    public static final String API_BASE_URL = "https://bhopper.wtf/nonsense/api";
    public static final URI SERVER_URI = URI.create("wss://bhopper.wtf");

    private String accessToken = "0";
    private Account account = Account.DEFAULT_ACCOUNT;

    private final WebSocketClient wsClient;
    private final PacketHandler packetHandler;

    public Universe() {
        this.wsClient = new WebSocketClient(this);
        this.packetHandler = new PacketHandler(this);
    }

    public void connect(URI uri, String accessToken) {
        this.wsClient.connect(uri, accessToken);
    }

    public void disconnect() {
        this.wsClient.close();
    }

    public boolean updateAccessToken(String username, String password) {
        try {
            Map<Object, Object> params = new HashMap<>();
            params.put("user", username);
            params.put("pass", password);
            Http http = new Http(API_BASE_URL + "/access")
                    .header("User-Agent", "Nonsense Client")
                    .postJson(params);

            if (http.status() != 200) {
                return false;
            }

            this.accessToken = http.body();
            return true;

        } catch (Exception e) {
            Nonsense.LOGGER.error(e);
            return false;
        }
    }

    public void sendPacket(AbstractClientPacket packet) {
        this.wsClient.sendPacket(packet);
    }

    public Account getAccount() {
        return this.account;
    }

    public PacketHandler getHandler() {
        return this.packetHandler;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

}
