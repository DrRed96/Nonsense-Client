package wtf.bhopper.nonsense.universe.packet.impl.server;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.universe.packet.PacketHandler;
import wtf.bhopper.nonsense.universe.packet.api.AbstractServerPacket;

public class S2CPacketKeepAlive extends AbstractServerPacket {

    private int key;

    public S2CPacketKeepAlive() {}

    @Override
    public void readData(JsonObject json) {
        this.key = json.get("key").getAsInt();
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handleKeepAlive(this);
    }

    public int getKey() {
        return this.key;
    }
}
