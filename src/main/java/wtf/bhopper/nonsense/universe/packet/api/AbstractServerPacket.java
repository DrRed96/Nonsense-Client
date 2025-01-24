package wtf.bhopper.nonsense.universe.packet.api;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.universe.packet.PacketHandler;

public abstract class AbstractServerPacket implements IPacket {

    @Override
    public JsonObject writeData() {
        return null;
    }

}
