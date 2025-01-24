package wtf.bhopper.nonsense.universe.packet.impl.client;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.universe.packet.api.AbstractClientPacket;

public class C2SPacketLogin extends AbstractClientPacket {

    public String accessToken;

    public C2SPacketLogin() {}

    public C2SPacketLogin(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public JsonObject writeData() {
        JsonObject object = new JsonObject();
        object.addProperty("access_token", accessToken);
        return object;
    }

}
