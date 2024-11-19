package wtf.bhopper.nonsense.util.misc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonUtil {

    public static void writeToFile(JsonObject object, File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(object.toString());
        writer.close();
    }

    public static JsonObject readFromFile(File file) throws IOException {
        return new JsonParser().parse(new FileReader(file)).getAsJsonObject();
    }

}
