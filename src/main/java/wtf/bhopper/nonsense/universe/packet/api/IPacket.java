package wtf.bhopper.nonsense.universe.packet.api;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.universe.packet.PacketHandler;

public interface IPacket {

    JsonObject writeData();

    void readData(JsonObject json);

    void handle(PacketHandler handler);

}
