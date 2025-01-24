package wtf.bhopper.nonsense.alt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.util.UUIDTypeAdapter;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.alt.loginthread.AbstractLoginThread;
import wtf.bhopper.nonsense.util.misc.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class AltManager {

    public static AbstractLoginThread loginThread = null;

    private final File altsFile;

    // Because the account login is done in a separate thread a CopyOnWriteArrayList is used for thread safety
    public Map<UUID, Alt> alts = new LinkedHashMap<>();

    public AltManager() {
        this.altsFile = new File(Nonsense.getDataDir(), "alts.json");
    }

    public Alt getFromUuid(UUID uuid) {
        return alts.getOrDefault(uuid, null);
    }

    public Alt getFromUuid(String uuid) {
        return this.getFromUuid(UUIDTypeAdapter.fromString(uuid));
    }

    public void addAccount(Alt account) {
        this.alts.put(account.getUuid(), account);
        this.trySave();
    }

    public void removeAccount(Alt account) {
        this.alts.remove(account.getUuid());
        this.trySave();
    }

    public void load() throws IOException {
        alts.clear();

        JsonObject json = JsonUtil.readFromFile(this.altsFile);

        JsonArray alts = json.getAsJsonArray("alts");
        for (JsonElement altElement : alts) {
            Alt alt = new Alt(altElement.getAsJsonObject());
            this.alts.put(alt.getUuid(), alt);
        }

    }

    public void tryLoad() {
        try {
            this.load();
        } catch (IOException exception) {
            Nonsense.LOGGER.error("Failed to load accounts", exception);
        }
    }

    public void save() throws IOException {

        JsonObject json = new JsonObject();
        JsonArray alts = new JsonArray();

        for (Alt alt : this.alts.values()) {
            alts.add(alt.toJson());
        }

        json.add("alts", alts);
        JsonUtil.writeToFile(json, this.altsFile);
    }

    public void trySave() {
        try {
            this.save();
        } catch (IOException exception) {
            Nonsense.LOGGER.error("Failed to save accounts", exception);
        }
    }

}
