package wtf.bhopper.nonsense.alt.loginthread;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.alt.Alt;
import wtf.bhopper.nonsense.alt.mslogin.LoginData;
import wtf.bhopper.nonsense.alt.mslogin.MSAuthException;
import wtf.bhopper.nonsense.alt.mslogin.MSAuthScheme;
import wtf.bhopper.nonsense.gui.screens.altmanager.GuiAltManager;
import wtf.bhopper.nonsense.util.misc.Http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class BrowserLoginThread extends AbstractLoginThread {

    public BrowserLoginThread(Consumer<LoginData> loginDataCallback, Consumer<Exception> errorCallback) {
        super(loginDataCallback, errorCallback);
    }

    @Override
    public void execute() {
        GuiAltManager.message = "Waiting...";

        try {
            this.startServer();
        } catch (Exception exception) {
            this.stopServer();
            this.errorCallback.accept(exception);
        }

    }

    @Override
    public void finish() {
        this.stopServer();
    }

    public void startServer() throws IOException {
        if (MSAuthScheme.server != null) {
            return;
        }

        MSAuthScheme.server = HttpServer.create(new InetSocketAddress("localhost", MSAuthScheme.PORT), 0);

        MSAuthScheme.server.createContext("/", new Handler());
        MSAuthScheme.server.createContext("/end", req -> {
            try {
                req.getResponseHeaders().put("Content-Type", Collections.singletonList("text/html; charset=UTF-8"));
                Http.writeText(req, "<html><body><h1>You may now close this page.</h1></body></html>");
            } catch (Exception exception) {
                Nonsense.LOGGER.error("Error displaying end message", exception);
            }
            BrowserLoginThread.this.stopServer();
        });
        MSAuthScheme.server.setExecutor(Executors.newSingleThreadExecutor());
        MSAuthScheme.server.start();

    }

    public void stopServer() {
        if (MSAuthScheme.server == null) {
            return;
        }

        MSAuthScheme.server.stop(0);
        MSAuthScheme.server = null;
    }

    public class Handler implements HttpHandler {

        @Override
        public void handle(HttpExchange req) throws IOException {
            if (req.getRequestMethod().equalsIgnoreCase("GET")) {

                req.getResponseHeaders().add("Location", "http://localhost:" + MSAuthScheme.PORT + "/end");
                req.sendResponseHeaders(302, -1L);

                List<NameValuePair> query = URLEncodedUtils.parse(req.getRequestURI(), StandardCharsets.UTF_8.name());

                boolean ok = false;

                for (NameValuePair pair : query) {
                    if (pair.getName().equals("code")) {
                        GuiAltManager.message = "Code received, logging in";

                        ok = true;

                        try {
                            MSAuthScheme.AuthTokenResponse res = MSAuthScheme.tokenFromCode(pair.getValue());
                            LoginData loginData = MSAuthScheme.quickLogin(res.access_token, res.refresh_token, Alt.Type.BROWSER);
                            BrowserLoginThread.this.loginDataCallback.accept(loginData);

                        } catch (Exception exception) {
                            errorCallback.accept(exception);
                        }
                        break;
                    }
                    if (pair.getName().equals("error")) {
                        BrowserLoginThread.this.errorCallback.accept(new MSAuthException(pair.getValue()));
                    }
                }

                if (!ok) {
                    BrowserLoginThread.this.errorCallback.accept(new MSAuthException("Code was not found"));
                }

            }

            BrowserLoginThread.this.finish();
        }
    }
}
