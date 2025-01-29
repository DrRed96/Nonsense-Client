package wtf.bhopper.nonsense.universe.packet.impl.client;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.universe.packet.api.AbstractClientPacket;

public class C2SPacketMessage extends AbstractClientPacket {

    private String message;
    private int color;

    public C2SPacketMessage() {}

    public C2SPacketMessage(String message, int color) {
        this.message = message;
        if (this.message.length() > 100) {
            this.message = message.substring(0, 100);
        }
        this.color = color;
    }

    @Override
    public JsonObject writeData() {
        JsonObject object = new JsonObject();
        object.addProperty("message", this.message);
        object.addProperty("color", this.color);
        return object;
    }

}
