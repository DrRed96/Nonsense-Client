package wtf.bhopper.nonsense.universe.packet.impl.server;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.universe.Account;
import wtf.bhopper.nonsense.universe.packet.PacketHandler;
import wtf.bhopper.nonsense.universe.packet.api.AbstractServerPacket;

public class S2CPacketMessage extends AbstractServerPacket {

    private String message;
    private Account account;
    private int color;

    @Override
    public void readData(JsonObject json) {
        this.message = json.get("message").getAsString();
        this.account = PacketHandler.readAccount(json.get("account").getAsJsonObject());
        this.color = json.get("color").getAsInt();
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handleMessage(this);
    }

    public String getMessage() {
        return this.message;
    }

    public Account getAccount() {
        return this.account;
    }

    public int getColor() {
        return this.color;
    }

}
