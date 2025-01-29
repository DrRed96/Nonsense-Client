package wtf.bhopper.nonsense.config;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.util.misc.JsonUtil;

import java.io.File;
import java.io.IOException;

public class Config {

    public final String name;
    private final File file;

    public Config(String name) {
        this(name, Nonsense.getConfigManager());
    }

    protected Config(String name, ConfigManager manager) {
        this.name = name;
        this.file = new File(manager.folder, name + ConfigManager.FILE_EXTENSION);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {}
        }
    }

    public File getFile() {
        return this.file;
    }

    public void save() throws IOException {
        JsonUtil.writeToFile(this.serialize(), this.file);
    }

    public void load() throws IOException {
        this.deserialize(JsonUtil.readFromFile(this.file));
    }


    public JsonObject serialize() {
        JsonObject root = new JsonObject();

        JsonObject modules = new JsonObject();
        for (AbstractModule module : Nonsense.getModuleManager().getModules()) {
            modules.add(module.name, module.serialize());
        }
        root.add("modules", modules);

        return root;
    }

    public void deserialize(JsonObject object) {

        JsonObject modules = object.getAsJsonObject("modules");

        for (AbstractModule module : Nonsense.getModuleManager().getModules()) {
            module.deserialize(modules.getAsJsonObject(module.name));
        }

    }

}
