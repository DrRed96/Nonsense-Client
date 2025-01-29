package wtf.bhopper.nonsense.universe;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventChat;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.module.impl.other.IrcMod;
import wtf.bhopper.nonsense.universe.packet.PacketHandler;
import wtf.bhopper.nonsense.universe.packet.api.AbstractClientPacket;
import wtf.bhopper.nonsense.universe.packet.impl.client.C2SPacketMessage;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;
import wtf.bhopper.nonsense.util.misc.Http;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Universe {

    public static final String API_BASE_URL = "https://bhopper.wtf/nonsense/api";
    public static final URI SERVER_URI = URI.create("wss://bhopper.wtf"); // TODO: add the option for users to add their own servers

    public static String lastError = "";

    private String accessToken = "0";
    private Account account = Account.DEFAULT_ACCOUNT;

    private WebSocketClient wsClient;
    private PacketHandler packetHandler;

    public Universe() {
        this.wsClient = new WebSocketClient(this);
        this.packetHandler = new PacketHandler(this);
        Nonsense.getEventBus().subscribe(this);
    }

    public void connect() {
        this.connect(SERVER_URI, this.accessToken);
    }

    public void connect(URI uri, String accessToken) {
        this.wsClient.connect(uri, accessToken);
    }

    public void disconnect() {
        this.wsClient.close();
    }

    @EventLink
    public final Listener<EventChat> onChat = event -> {
        String prefix = mod().chatPrefix.get();
        if (event.message.startsWith(prefix)) {
            event.cancel();

            if (!mod().isToggled()) {
                ChatUtil.error("IRC is not enabled.");
                return;
            }

            if (!this.wsClient.isOpen()) {
                ChatUtil.error("IRC is not connected.");
                return;
            }

            this.sendPacket(new C2SPacketMessage(event.message.substring(prefix.length()), Hud.color()));
        }
    };

    public boolean updateAccessToken(String username, String password) {
        try {
            Map<Object, Object> params = new HashMap<>();
            params.put("user", username);
            params.put("pass", password);
            Http http = new Http(API_BASE_URL + "/access")
                    .header("User-Agent", Nonsense.NAME)
                    .postJson(params);

            if (http.status() != 200) {
                lastError = http.body();
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

    public boolean isConnected() {
        return wsClient.isOpen();
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

    public static IrcMod mod() {
        return Nonsense.module(IrcMod.class);
    }

}
