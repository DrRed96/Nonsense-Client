package wtf.bhopper.nonsense.alt;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.Session;
import wtf.bhopper.nonsense.alt.loginthread.CookieLoginThread;
import wtf.bhopper.nonsense.alt.loginthread.LoginDataCallback;
import wtf.bhopper.nonsense.alt.mslogin.LoginData;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.gui.screens.altmanager.GuiAltManager;
import wtf.bhopper.nonsense.util.misc.ErrorCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Alt {

    private final Type type;

    private String accessToken;
    private String meta;

    private UUID uuid;
    private String username;

    private BanStatus banStatus = BanStatus.NEW;
    private long unbanTime = 0L;
    private String banReason = "";

    public Alt(Type type, LoginData loginData) {
        this.type = type;
        this.accessToken = loginData.accessToken;
        this.meta = loginData.meta;
        this.uuid = loginData.uuid;
        this.username = loginData.username;
    }

    public Alt(JsonObject json) {
        this.type = Type.values()[JsonUtils.getInt(json, "type", 0)];
        this.accessToken = JsonUtils.getString(json, "accessToken", "0");
        this.meta = JsonUtils.getString(json, "meta", "0");
        this.uuid = UUIDTypeAdapter.fromString(JsonUtils.getString(json, "uuid", "0"));
        this.username = JsonUtils.getString(json, "username", "0");
        this.banStatus = BanStatus.values()[JsonUtils.getInt(json, "banStatus", 0)];
        this.unbanTime = JsonUtils.getLong(json, "banTimer", 0L);
        this.banReason = JsonUtils.getString(json, "banReason", "BAN");
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", this.type.ordinal());
        json.addProperty("accessToken", this.accessToken);
        json.addProperty("meta", this.meta);
        json.addProperty("uuid", UUIDTypeAdapter.fromUUID(this.uuid));
        json.addProperty("username", this.username);
        json.addProperty("banStatus", this.banStatus.ordinal());
        json.addProperty("unbanTime", this.unbanTime);
        json.addProperty("banReason", this.banReason);
        return json;
    }

    public Session getSession() {
        return new Session(this.username, UUIDTypeAdapter.fromUUID(this.uuid), this.accessToken, "microsoft");
    }

    public GameProfile getProfile() {
        return new GameProfile(this.uuid, this.username);
    }

    public void login() {
        Minecraft.getMinecraft().setSession(this.getSession());
    }

    public Type getType() {
        return this.type;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String geMeta() {
        return this.meta;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public BanStatus getBanStatus() {
        return this.banStatus;
    }

    public long getUnbanTime() {
        return this.unbanTime;
    }

    public void refreshAccount() {

        switch (type) {

            case MICROSOFT:
            case BROWSER:
            case REFRESH:
                // All will work with the refresh token
                break;

            case COOKIE:
                new CookieLoginThread(this.meta, loginData -> {
                    this.accessToken = loginData.accessToken;
                    this.meta = loginData.meta;
                    this.uuid = loginData.uuid;
                    this.username = loginData.username;
                    Notification.send("Alt Manager", "Refreshed account: " + loginData.username, NotificationType.SUCCESS, 3000);
                }, error -> {
                    GuiAltManager.lastError = error;
                    Notification.send("Alt Manager", "Failed to refresh account: " + error.getMessage(), NotificationType.ERROR, 3000);
                }).start();
                break;

            case SESSION:
                Notification.send("Alt Manager", "Session alts cannot be refreshed", NotificationType.ERROR, 3000);
                break;

        }

    }

    public String getBanReason() {
        return this.banReason;
    }

    public String getBanStatusString() {

        if (this.type == Type.OFFLINE) {
            return "\2477Offline Account";
        }

        if (this.banStatus == BanStatus.UNBANNED) {
            return "\247aUnbanned";
        }

        if (this.banStatus == BanStatus.TEMP_BANNED) {
            long banTime = this.unbanTime - System.currentTimeMillis();
            long days = TimeUnit.MILLISECONDS.toDays(banTime);
            long hours = TimeUnit.MILLISECONDS.toHours(banTime) - (days * 24);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(banTime) - (TimeUnit.MILLISECONDS.toHours(banTime) * 60);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(banTime) - (TimeUnit.MILLISECONDS.toMinutes(banTime) * 60);
            List<String> banTimeFormats = new ArrayList<>();
            if (days > 0L) {
                banTimeFormats.add(days + "d");
            }
            if (hours > 0L) {
                banTimeFormats.add(hours + "h");
            }
            if (minutes > 0L) {
                banTimeFormats.add(days + "m");
            }
            if (seconds > 0L) {
                banTimeFormats.add(days + "s");
            }

            return "\247cTemporarily Banned (" + String.join(" ", banTimeFormats) + ")";
        }

        if (this.banStatus == BanStatus.PERM_BANNED) {
            return "\247cPermanently Banned";
        }

        return "\2477New";
    }

    public void setUnbanned() {
        this.banStatus = BanStatus.UNBANNED;
        this.unbanTime = 0L;
        this.banReason = "";
    }

    public void setTempBanned(long unbanTime, String banReason) {
        this.banStatus = BanStatus.TEMP_BANNED;
        this.unbanTime = unbanTime;
        this.banReason = banReason;
    }

    public void setPermBanned(String banReason) {
        this.banStatus = BanStatus.PERM_BANNED;
        this.unbanTime = 0L;
        this.banReason = banReason;
    }

    public enum Type {
        MICROSOFT("Microsoft"),
        COOKIE("Cookie"),
        BROWSER("Browser"),
        SESSION("Session"),
        REFRESH("Refresh"),
        OFFLINE("Offline");

        public final String name;

        Type(String name) {
            this.name = name;
        }
    }

    public enum BanStatus {
        NEW,
        UNBANNED,
        TEMP_BANNED,
        PERM_BANNED
    }

    public static final Alt DEFAULT = new Alt(Type.OFFLINE, new LoginData(Type.OFFLINE, "0", UUID.randomUUID(), "Player", "0"));

}
