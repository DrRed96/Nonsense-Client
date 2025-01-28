package wtf.bhopper.nonsense.universe.packet.impl.client;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.universe.packet.api.AbstractClientPacket;

public class C2SPacketRequestDiscordLink extends AbstractClientPacket {
    @Override
    public JsonObject writeData() {
        return new JsonObject();
    }
}
