package wtf.bhopper.nonsense.universe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.glassfish.tyrus.client.ClientManager;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.universe.packet.PacketRegistry;
import wtf.bhopper.nonsense.universe.packet.api.AbstractClientPacket;
import wtf.bhopper.nonsense.universe.packet.api.IPacket;
import wtf.bhopper.nonsense.universe.packet.impl.client.C2SPacketLogin;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class WebSocketClient {

    private final Universe universe;
    private final JsonParser parser = new JsonParser();

    private String accessToken;
    private Session session = null;

    public WebSocketClient(Universe universe) {
        this.universe = universe;
    }

    public void connect(URI uri, String accessToken) {
        this.accessToken = accessToken;
        try {
            ClientManager client = ClientManager.createClient();
            client.connectToServer(this, uri);
        } catch (DeploymentException | IOException e) {}
    }

    public void close() {
        try {
            if (this.session != null) {
                this.session.close();
            }
        } catch (IOException _) {}
    }

    public boolean isOpen() {
        return this.session != null && this.session.isOpen();
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        this.sendPacket(new C2SPacketLogin(this.accessToken));
    }

    @OnMessage
    public void onMessage(String message) {
        try {

            JsonObject root = this.parser.parse(message).getAsJsonObject();
            byte packetId = root.get("id").getAsByte();
            JsonObject data = root.get("data").getAsJsonObject();

            IPacket packet = PacketRegistry.SERVER2CLIENT.getPacket(packetId);
            packet.readData(data);
            packet.handle(this.universe.getHandler());

        } catch (Exception e) {
            Nonsense.LOGGER.error("Failed to decode packet: {}", message);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        Notification.send("Universe", "Disconnected (" + reason.getCloseCode().getCode() + "): " + reason.getReasonPhrase(), NotificationType.WARNING, 5000);
    }

    public void sendPacket(AbstractClientPacket packet) {

        if (this.session == null) {
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty("id", PacketRegistry.CLIENT2SERVER.getPacketId(packet));
        json.add("data", packet.writeData());
        String data = json.toString();
        this.session.getAsyncRemote().sendText(data);
    }

}
