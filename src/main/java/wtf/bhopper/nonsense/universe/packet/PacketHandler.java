package wtf.bhopper.nonsense.universe.packet;

import com.google.gson.JsonObject;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.EnumChatFormatting;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.universe.Account;
import wtf.bhopper.nonsense.universe.Universe;
import wtf.bhopper.nonsense.universe.packet.api.AbstractClientPacket;
import wtf.bhopper.nonsense.universe.packet.impl.client.C2SPacketKeepAlive;
import wtf.bhopper.nonsense.universe.packet.impl.server.*;
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
            Notification.send("Universe", "Successfully logged in: " + account.username() + " (UID: " + account.id() + ")", NotificationType.SUCCESS, 3000);
        } else {
            Notification.send("Universe", "Failed to login: " + packet.getMessage(), NotificationType.ERROR, 5000);
        }
    }

    public void handleKeepAlive(S2CPacketKeepAlive packet) {
        this.sendPacket(new C2SPacketKeepAlive(packet.getKey()));
    }

    public void handleMessage(S2CPacketMessage packet) {
        ChatUtil.irc(packet.getMessage(), packet.getAccount(), packet.getColor());
    }

    public void handleDiscordCode(S2CPacketDiscordCode packet) {
        String code = packet.getCode();

        ChatUtil.Builder.of("%s Your linking code: \247a%s\2477, it will be valid for 10 minutes.", ChatUtil.CHAT_PREFIX_SHORT, code)
                .setColor(EnumChatFormatting.GRAY)
                .setClickEvent(ClickEvent.Action.RUN_COMMAND, ".copy " + code)
                .setHoverEvent("Click to copy code!")
                .send();
    }

    public void handleWhisper(S2CPacketWhisper packet) {
        ChatUtil.whisper(packet.getMessage(), packet.getAccount(), packet.getDirection() == S2CPacketWhisper.Direction.OUTGOING);
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
