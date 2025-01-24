package wtf.bhopper.nonsense.universe.packet;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.universe.Account;
import wtf.bhopper.nonsense.universe.Universe;
import wtf.bhopper.nonsense.universe.packet.api.AbstractClientPacket;
import wtf.bhopper.nonsense.universe.packet.impl.client.C2SPacketKeepAlive;
import wtf.bhopper.nonsense.universe.packet.impl.server.S2CPacketKeepAlive;
import wtf.bhopper.nonsense.universe.packet.impl.server.S2CPacketLoginResponse;
import wtf.bhopper.nonsense.universe.packet.impl.server.S2CPacketMessage;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;

public class PacketHandler {

    private final Universe universe;

    public PacketHandler(Universe universe) {
        this.universe = universe;
    }

    public void handleLoginResponse(S2CPacketLoginResponse packet) {
        if (packet.getSuccess()) {
            Account account = packet.getAccount();
            this.universe.setAccount(account);
            Notification.send("Universe", "Login Success: " + account, NotificationType.SUCCESS, 3000);
        } else {
            Notification.send("Universe", "Login failure: " + packet.getMessage(), NotificationType.ERROR, 5000);
        }
    }

    public void handleKeepAlive(S2CPacketKeepAlive packet) {
        this.sendPacket(new C2SPacketKeepAlive(packet.getKey()));
    }

    public void handleMessage(S2CPacketMessage packet) {
        ChatUtil.irc(packet.getMessage(), packet.getAccount());
    }

    public void sendPacket(AbstractClientPacket packet) {
        this.universe.sendPacket(packet);
    }

    public static Account readAccount(JsonObject object) {
        int id = object.get("id").getAsInt();
        String username = object.get("username").getAsString();
        Account.Rank rank = Account.Rank.valueOf(object.get("rank").getAsString());
        return new Account(id, username, rank);
    }

}
