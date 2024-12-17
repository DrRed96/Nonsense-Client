package wtf.bhopper.nonsense.alt.mslogin;

import com.mojang.util.UUIDTypeAdapter;
import com.sun.net.httpserver.HttpServer;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.alt.Alt;
import wtf.bhopper.nonsense.gui.screens.altmanager.GuiAltManager;
import wtf.bhopper.nonsense.util.misc.Http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MSAuthScheme {

    public static final String CLIENT_ID = "54fd49e4-2103-4044-9603-2b028c814ec3";
    public static final int PORT = 59125;

    public static HttpServer server = null;

    public static final String OAUTH20_TOKEN_LINK = "https://login.live.com/oauth20_token.srf";
    public static final String XBL_LINK = "https://user.auth.xboxlive.com/user/authenticate";
    public static final String XSTS_LINK = "https://xsts.auth.xboxlive.com/xsts/authorize";
    public static final String MC_SERVICES_LINK = "https://api.minecraftservices.com/authentication/login_with_xbox";
    public static final String OWNERSHIP_LINK = "https://api.minecraftservices.com/entitlements/mcstore";
    public static final String PROFILE_LINK = "https://api.minecraftservices.com/minecraft/profile";

    public static final String LOGIN_URL = "https://login.live.com/oauth20_authorize.srf?client_id=" + CLIENT_ID + "&response_type=code&redirect_uri=http://localhost:" + PORT + "&scope=XboxLive.signin%20offline_access&prompt=select_account";

    public static LoginData quickLogin(String accessToken, String meta, Alt.Type type) throws Exception {
        XblXstsResponse xblRes = xboxLiveAuth(accessToken);
        XblXstsResponse xstsRes = xstsToken(xblRes.Token);
        McResponse mcRes = minecraftAuth(xblRes.DisplayClaims.xui[0].uhs, xstsRes.Token);
        GameOwnershipResponse ownershipRes = checkGameOwnership(mcRes.access_token);

        if (!ownershipRes.hasGameOwnership()) {
            throw new MSAuthException("That account does not own Minecraft");
        }

        ProfileResponse profileRes = minecraftProfile(mcRes.access_token);

        return new LoginData(type, mcRes.access_token, profileRes.id, profileRes.name, meta);
    }

    public static AuthTokenResponse tokenFromCode(String code) throws Exception {
        Map<Object, Object> params = new HashMap<>();
        params.put("client_id", CLIENT_ID);
        params.put("code", code);
        params.put("grant_type", "authorization_code");
        params.put("redirect_uri", "http://localhost:" + PORT);

        Http http = new Http(OAUTH20_TOKEN_LINK)
                .header("Accept", "application/json")
                .postUrlEncoded(params);

        if (http.status() != 200) {
            throw GuiAltManager.lastError = new MSAuthException("Xbox Live Authentication returned invalid status code: " + http.status() + ", " + http.body());
        }

        return Nonsense.GSON.fromJson(http.body(), AuthTokenResponse.class);
    }

    public static AuthTokenResponse tokenFromRefresh(String refreshToken) throws Exception {
        Map<Object, Object> params = new HashMap<>();
        params.put("client_id", CLIENT_ID);
        params.put("refresh_token", refreshToken);
        params.put("grant_type", "refresh_token");
        params.put("redirect_uri", "http://localhost:" + PORT);

        Http http = new Http(OAUTH20_TOKEN_LINK)
                .header("Accept", "application/json")
                .postUrlEncoded(params);

        if (http.status() != 200) {
            throw GuiAltManager.lastError = new MSAuthException("Xbox Live Authentication returned invalid status code: " + http.status() + ", " + http.body());
        }

        return Nonsense.GSON.fromJson(http.body(), AuthTokenResponse.class);
    }

    public static XblXstsResponse xboxLiveAuth(String token) throws Exception {

        GuiAltManager.message = "Authenticating with Xbox Live";

        Map<Object, Object> properties = new HashMap<>();
        properties.put("AuthMethod", "RPS");
        properties.put("SiteName", "user.auth.xboxlive.com");
        properties.put("RpsTicket", "d=" + token);

        Map<Object, Object> params = new HashMap<>();
        params.put("Properties", properties);
        params.put("RelyingParty", "http://auth.xboxlive.com");
        params.put("TokenType", "JWT");

        Http http = new Http(XBL_LINK)
                .header("Accept", "application/json")
                .postJson(params);

        if (http.status() != 200) {
            throw GuiAltManager.lastError = new MSAuthException("Xbox Live Authentication returned invalid status code: " + http.status());
        }

        return Nonsense.GSON.fromJson(http.body(), XblXstsResponse.class);
    }

    public static XblXstsResponse xstsToken(String xblToken) throws Exception {

        GuiAltManager.message = "Fetching XSTS Token";

        Map<Object, Object> properties = new HashMap<>();
        properties.put("SandboxId", "RETAIL");
        properties.put("UserTokens", Collections.singletonList(xblToken));

        Map<Object, Object> params = new HashMap<>();
        params.put("Properties", properties);
        params.put("RelyingParty", "rp://api.minecraftservices.com/");
        params.put("TokenType", "JWT");

        Http http = new Http(XSTS_LINK)
                .header("Accept", "application/json")
                .postJson(params);

        if (http.status() != 200) {
            throw GuiAltManager.lastError = new MSAuthException("Xbox Live Authentication returned invalid status code: " + http.status());
        }

        return Nonsense.GSON.fromJson(http.body(), XblXstsResponse.class);
    }

    public static McResponse minecraftAuth(String xblClaim, String xstsToken) throws Exception {

        GuiAltManager.message = "Authenticating with Minecraft";

        Map<Object, Object> params = new HashMap<>();
        params.put("identityToken", "XBL3.0 x=" + xblClaim + ";" + xstsToken);

        Http http = new Http(MC_SERVICES_LINK)
                .header("Accept", "application/json")
                .postJson(params);

        if (http.status() != 200) {
            throw new MSAuthException("Minecraft Authentication returned invalid status: " + http.status());
        }

        return Nonsense.GSON.fromJson(http.body(), McResponse.class);
    }

    public static GameOwnershipResponse checkGameOwnership(String mcToken) throws Exception {

        GuiAltManager.message = "Checking game ownership";

        Http http = new Http(OWNERSHIP_LINK)
                .header("Authorization", "Bearer " + mcToken)
                .header("Accept", "application/json")
                .get();

        if (http.status() != 200) {
            throw GuiAltManager.lastError = new MSAuthException("Xbox Live Authentication returned invalid status code: " + http.status());
        }

        return Nonsense.GSON.fromJson(http.body(), GameOwnershipResponse.class);
    }

    public static ProfileResponse minecraftProfile(String mcToken) throws Exception {

        GuiAltManager.message = "Fetching Minecraft profile";

        Http http = new Http(PROFILE_LINK)
                .header("Authorization", "Bearer " + mcToken)
                .header("Accept", "application/json")
                .get();

        if (http.status() != 200) {
            throw GuiAltManager.lastError = new MSAuthException("Xbox Live Authentication returned invalid status code: " + http.status());
        }

        return Nonsense.GSON.fromJson(http.body(), ProfileResponse.class);
    }

    public static class AuthTokenResponse {
        public String access_token;
        public String refresh_token;
    }

    public static class XblXstsResponse {
        public String Token;
        public DisplayClaims DisplayClaims;

        public static class DisplayClaims {
            public Claim[] xui;

            public static class Claim {
                public String uhs;
            }
        }
    }

    public static class McResponse {
        public String access_token;
    }

    public static class GameOwnershipResponse {
        public Item[] items;

        public static class Item {
            private String name;
        }

        public boolean hasGameOwnership() {
            boolean hasProduct = false;
            boolean hasGame = false;

            for (Item item : items) {
                if (item.name.equals("product_minecraft")) {
                    hasProduct = true;
                } else if (item.name.equals("game_minecraft")) {
                    hasGame = true;
                }
            }

            return hasProduct && hasGame;
        }
    }

    public static class ProfileResponse {
        public String id;
        public String name;
    }

}
