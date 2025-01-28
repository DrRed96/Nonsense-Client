package wtf.bhopper.nonsense.universe.packet.impl.server;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.universe.Account;
import wtf.bhopper.nonsense.universe.packet.PacketHandler;
import wtf.bhopper.nonsense.universe.packet.api.AbstractServerPacket;

public class S2CPacketWhisper extends AbstractServerPacket {

    private String message;
    private Account account;
    private Direction direction;

    @Override
    public void readData(JsonObject json) {
        this.message = json.get("message").getAsString();
        this.account = PacketHandler.readAccount(json.get("account").getAsJsonObject());
        this.direction = Direction.valueOf(json.get("direction").getAsString());
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handleWhisper(this);
    }

    public String getMessage() {
        return this.message;
    }

    public Account getAccount() {
        return this.account;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public enum Direction {
        INCOMING,
        OUTGOING
    }

}
