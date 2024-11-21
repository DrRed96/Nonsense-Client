package wtf.bhopper.nonsense.alt.mslogin;

import com.mojang.util.UUIDTypeAdapter;
import wtf.bhopper.nonsense.alt.Alt;

import java.util.UUID;

public class LoginData {

    public final Alt.Type type;
    public final String accessToken;
    public final UUID uuid;
    public final String username;
    public final String meta;

    public LoginData(Alt.Type type, String accessToken, UUID uuid, String username, String meta) {
        this.type = type;
        this.accessToken = accessToken;
        this.uuid = uuid;
        this.username = username;
        this.meta = meta;
    }

    public LoginData(Alt.Type type, String accessToken, String uuid, String username, String meta) {
        this.type = type;
        this.accessToken = accessToken;
        this.uuid = UUIDTypeAdapter.fromString(uuid);
        this.username = username;
        this.meta = meta;
    }
}
