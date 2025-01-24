package wtf.bhopper.nonsense.universe.packet.api;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.universe.packet.PacketHandler;

public abstract class AbstractClientPacket implements IPacket {

    @Override
    public void readData(JsonObject json) { }

    @Override
    public void handle(PacketHandler handler) { }

}
