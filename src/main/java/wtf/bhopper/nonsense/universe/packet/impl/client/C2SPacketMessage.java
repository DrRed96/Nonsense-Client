package wtf.bhopper.nonsense.universe.packet.impl.client;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.universe.packet.api.AbstractClientPacket;

public class C2SPacketMessage extends AbstractClientPacket {

    private String message;

    public C2SPacketMessage() {}

    public C2SPacketMessage(String message) {
        this.message = message;
    }

    @Override
    public JsonObject writeData() {
        JsonObject object = new JsonObject();
        object.addProperty("access_token", this.message);
        return object;
    }

}
