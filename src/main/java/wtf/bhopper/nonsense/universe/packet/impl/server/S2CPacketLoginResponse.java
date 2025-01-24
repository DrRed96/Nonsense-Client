package wtf.bhopper.nonsense.universe.packet.impl.server;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.universe.Account;
import wtf.bhopper.nonsense.universe.packet.PacketHandler;
import wtf.bhopper.nonsense.universe.packet.api.AbstractServerPacket;

public class S2CPacketLoginResponse extends AbstractServerPacket {

    private boolean success;
    private String message;
    private Account account;

    public S2CPacketLoginResponse() {}

    @Override
    public void readData(JsonObject json) {
        this.success = json.get("success").getAsBoolean();
        this.message = json.get("message").getAsString();
        if (this.success) {
            this.account = PacketHandler.readAccount(json.get("account").getAsJsonObject());
        }
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handleLoginResponse(this);
    }

    public boolean getSuccess() {
        return this.success;
    }

    public String getMessage() {
        return this.message;
    }

    public Account getAccount() {
        return this.account;
    }

}
