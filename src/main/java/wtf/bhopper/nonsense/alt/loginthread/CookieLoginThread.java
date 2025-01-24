package wtf.bhopper.nonsense.alt.loginthread;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.alt.Alt;
import wtf.bhopper.nonsense.alt.mslogin.LoginData;
import wtf.bhopper.nonsense.alt.mslogin.MSAuthException;
import wtf.bhopper.nonsense.alt.mslogin.MSAuthScheme;
import wtf.bhopper.nonsense.gui.screens.altmanager.GuiAltManager;
import wtf.bhopper.nonsense.util.misc.Http;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CookieLoginThread extends AbstractLoginThread {

    private static final String COOKIE_URL = "https://sisu.xboxlive.com/connect/XboxLive/?state=login&cobrandId=8058f65d-ce06-4c30-9559-473c9275a65d&tid=896928775&ru=https%3A%2F%2Fwww.minecraft.net%2Fen-us%2Flogin&aid=1142970254";

    private final File file;
    private String cookieHeader = null;

    public CookieLoginThread(File file, Consumer<LoginData> loginDataCallback, Consumer<Exception> errorCallback) {
        super(loginDataCallback, errorCallback);
        this.file = file;
    }

    public CookieLoginThread(String header, Consumer<LoginData> loginDataCallback, Consumer<Exception> errorCallback) {
        super(loginDataCallback, errorCallback);
        this.file = null;
        this.cookieHeader = header;
    }

    @Override
    public void execute() {
        try {

            GuiAltManager.message = "Logging in to Cookie alt...";

            if (this.cookieHeader == null) {
                List<Cookie> cookies = this.parseCookies();
                this.cookieHeader = this.buildCookieHeader(cookies);
                Nonsense.LOGGER.info("Cookie Header: {}", this.cookieHeader);
            }

            Map<String, String> headers = new HashMap<>();
            headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
            headers.put("Accept-Encoding", "gzip, deflate, br");
            headers.put("Accept-Language", "fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7");
            headers.put("Cookie", this.cookieHeader);
            headers.put("User-Agent", Http.FIREFOX_USER_AGENT);

            Http http1 = new Http(COOKIE_URL)
                    .headers(headers)
                    .get();
            if (http1.status() != 302) {
                throw new MSAuthException("(1) Request to " + COOKIE_URL + " returned status " + http1.status());
            }

            String location2 = http1.getHeader("location").replace(" ", "");
            Http http2 = new Http(location2)
                    .headers(headers)
                    .get();
            if (http2.status() != 302) {
                throw new MSAuthException("(2) Request to " + location2 + " returned status " + http2.status());
            }

            Http http3 = new Http(http2.getHeader("location"))
                    .headers(headers)
                    .get();
            if (http3.status() != 302) {
                throw new MSAuthException("(3) Request to " + http2.getHeader("location") + " returned status " + http3.status());
            }

            String accessToken = http3.getHeader("location").split("accessToken=")[1];
            String decoded = new String(Base64.getDecoder().decode(accessToken), StandardCharsets.UTF_8).split("\"rp://api\\.minecraftservices.com/\",")[1];
            String token = decoded.split("\"Token\":\"")[1].split("\"")[0];
            String uhs = decoded.split("\\{\"DisplayClaims\":\\{\"xui\":\\[\\{\"uhs\":\"")[1].split("\"")[0];

            MSAuthScheme.McResponse mcAuth = MSAuthScheme.minecraftAuth(uhs, token);
            MSAuthScheme.GameOwnershipResponse gameOwnership = MSAuthScheme.checkGameOwnership(mcAuth.access_token);
            if (!gameOwnership.hasGameOwnership()) {
                throw new MSAuthException("That user does not own Minecraft");
            }

            MSAuthScheme.ProfileResponse profileResponse = MSAuthScheme.minecraftProfile(mcAuth.access_token);
            loginDataCallback.accept(new LoginData(Alt.Type.COOKIE, mcAuth.access_token, profileResponse.id, profileResponse.name, this.cookieHeader));

        } catch (Exception exception) {
            this.errorCallback.accept(exception);
        }
    }

    @Override
    public void finish() {

    }

    private List<Cookie> parseCookies() throws IOException {
        List<Cookie> cookies = new ArrayList<>();
        try (Scanner scanner = new Scanner(this.file)) {
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                String[] parts = data.split("\t");

                if (parts.length != 7 || parts[0].startsWith("#")) {
                    continue;
                }

                String name = parts[5].trim();
                String value = parts[6].trim().replace("\r", "");
                String domain = parts[0].trim();
                String path = parts[2].trim();
                String sameSite = "Lax";
                boolean secure = parts[3].trim().equalsIgnoreCase("true");
                double expires = Double.parseDouble(parts[4].trim()) * 1000;

                if (domain.charAt(0) == '\ufeff') {
                    domain = domain.substring(1);
                }
                domain = domain.replaceAll("[^\\x20-\\x7E]", "");

                if (name.startsWith("__Host-")) {
                    secure = true;
                }

                if (name.contains("MS") && name.contains("AUTH")) {
                    cookies.add(new Cookie(name, value, domain, path, sameSite, secure, expires));
                }
            }
        }

        return cookies;
    }

    private String buildCookieHeader(List<Cookie> cookies) {
        return String.join("; ", cookies.stream().collect(
                        ArrayList::new,
                        (strings, cookie) -> strings.add(String.format("%s=%s", cookie.name, cookie.value)),
                        (BiConsumer<List<String>, List<String>>) List::addAll));
    }

    private static class Cookie {
        public final String name;
        public final String value;
        public final String domain;
        public final String path;
        public final String sameSite;
        public final boolean secure;
        public final double expires;

        public Cookie(String name, String value, String domain, String path, String sameSite, boolean secure, double expires) {
            this.name = name;
            this.value = value;
            this.domain = domain;
            this.path = path;
            this.sameSite = sameSite;
            this.secure = secure;
            this.expires = expires;
        }
    }
}
