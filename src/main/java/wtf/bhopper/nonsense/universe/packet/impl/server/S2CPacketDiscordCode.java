package wtf.bhopper.nonsense.universe.packet.impl.server;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.universe.packet.PacketHandler;
import wtf.bhopper.nonsense.universe.packet.api.AbstractServerPacket;

public class S2CPacketDiscordCode extends AbstractServerPacket {

    private String code;

    @Override
    public void readData(JsonObject json) {
        this.code = json.get("code").getAsString();
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handleDiscordCode(this);
    }

    public String getCode() {
        return this.code;
    }
}
