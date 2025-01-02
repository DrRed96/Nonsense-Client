package wtf.bhopper.nonsense.config;

import com.google.gson.JsonElement;

public interface ISerializable {

    JsonElement serialize();

    void deserialize(JsonElement element);

}
