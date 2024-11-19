package wtf.bhopper.nonsense.config;

import wtf.bhopper.nonsense.Nonsense;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    public static final String FILE_EXTENSION = ".json";

    public final File folder;
    public final List<Config> configs = new ArrayList<>();

    public ConfigManager() {
        folder = new File(Nonsense.getDataDir(), "configs");
        folder.mkdirs();
        this.reloadConfigs();
    }

    public void reloadConfigs() {
        this.configs.clear();

        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                this.configs.add(new Config(name.substring(0, name.length() - FILE_EXTENSION.length()), this));
            }
        }
    }

    public boolean hasConfig(String name) {
        return this.configs.stream().anyMatch(c -> c.name.equalsIgnoreCase(name));
    }

    public Config getConfig(String name) {
        return this.configs.stream()
                .filter(c -> c.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(new Config(name));
    }

    public void loadDefaultConfig() {
        try {
            Config config = this.getConfig("default");

            if (this.hasConfig("default")) {
                config.load();
            } else {
                config.save();
                this.configs.add(config);
            }
        } catch (IOException ignored) {}
    }

}
