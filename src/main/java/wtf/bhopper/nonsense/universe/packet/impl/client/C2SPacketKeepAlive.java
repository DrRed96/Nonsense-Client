package wtf.bhopper.nonsense.universe.packet.impl.client;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.universe.packet.api.AbstractClientPacket;

public class C2SPacketKeepAlive extends AbstractClientPacket {

    private int key;

    public C2SPacketKeepAlive() {}

    public C2SPacketKeepAlive(int key) {
        this.key = key;
    }

    @Override
    public JsonObject writeData() {
        JsonObject json = new JsonObject();
        json.addProperty("key", key);
        return json;
    }
}
