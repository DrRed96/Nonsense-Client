package wtf.bhopper.nonsense.universe.packet.impl.client;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.universe.packet.api.AbstractClientPacket;

public class C2SPacketWhisper extends AbstractClientPacket {

    private final String user;
    private String message;

    public C2SPacketWhisper(String user, String message) {
        this.user = user;
        this.message = message;
        if (this.message.length() > 100) {
            this.message = message.substring(0, 100);
        }
    }

    @Override
    public JsonObject writeData() {
        JsonObject json = new JsonObject();
        json.addProperty("user", this.user);
        json.addProperty("message", this.message);
        return json;
    }
}
