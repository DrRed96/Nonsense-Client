package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;

import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class Main {
    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        parser.accepts("demo");
        parser.accepts("fullscreen");
        parser.accepts("checkGlErrors");
        OptionSpec<String> optionServer = parser.accepts("server").withRequiredArg();
        OptionSpec<Integer> optionPort = parser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
        OptionSpec<File> optionGameDir = parser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
        OptionSpec<File> optionAssetsDir = parser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        OptionSpec<File> optionResourcePackDir = parser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        OptionSpec<String> optionProxyHost = parser.accepts("proxyHost").withRequiredArg();
        OptionSpec<Integer> optionProxyPort = parser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
        OptionSpec<String> optionProxyUser = parser.accepts("proxyUser").withRequiredArg();
        OptionSpec<String> optionProxyPass = parser.accepts("proxyPass").withRequiredArg();
        OptionSpec<String> optionUsername = parser.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.getSystemTime() % 1000L);
        OptionSpec<String> optionUuid = parser.accepts("uuid").withRequiredArg();
        OptionSpec<String> optionAccessToken = parser.accepts("accessToken").withRequiredArg().required();
        OptionSpec<String> optionVersion = parser.accepts("version").withRequiredArg().required();
        OptionSpec<Integer> optionWidth = parser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
        OptionSpec<Integer> optionHeight = parser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
        OptionSpec<String> optionUserProperties = parser.accepts("userProperties").withRequiredArg().defaultsTo("{}");
        OptionSpec<String> optionProfileProperties = parser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
        OptionSpec<String> optionAssetIndex = parser.accepts("assetIndex").withRequiredArg();
        OptionSpec<String> optionUserType = parser.accepts("userType").withRequiredArg().defaultsTo("legacy");
        OptionSpec<String> nonOptions = parser.nonOptions();
        OptionSet optionset = parser.parse(args);

        List<String> ignoredArgs = optionset.valuesOf(nonOptions);
        if (!ignoredArgs.isEmpty()) {
            System.out.println("Completely ignored arguments: " + ignoredArgs);
        }

        String proxyHost = optionset.valueOf(optionProxyHost);
        Proxy proxy = Proxy.NO_PROXY;

        if (proxyHost != null) {
            try {
                proxy = new Proxy(Type.SOCKS, new InetSocketAddress(proxyHost, optionset.valueOf(optionProxyPort)));
            } catch (Exception ignored) {

            }
        }

        final String proxyUser = optionset.valueOf(optionProxyUser);
        final String proxyPass = optionset.valueOf(optionProxyPass);

        if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(proxyUser) && isNullOrEmpty(proxyPass)) {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(proxyUser, proxyPass.toCharArray());
                }
            });
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PropertyMap.class, new Serializer())
                .create();

        // User Info
        String playerID = optionset.has(optionUuid) ? optionUuid.value(optionset) : optionUsername.value(optionset);
        Session session = new Session(optionUsername.value(optionset), playerID, optionAccessToken.value(optionset), optionUserType.value(optionset));
        PropertyMap userProperties = gson.fromJson(optionset.valueOf(optionUserProperties), PropertyMap.class);
        PropertyMap profileProperties = gson.fromJson(optionset.valueOf(optionProfileProperties), PropertyMap.class);

        // Display Info
        int width = optionset.valueOf(optionWidth);
        int height = optionset.valueOf(optionHeight);
        boolean fullscreen = optionset.has("fullscreen");
        boolean checkGlErrors = optionset.has("checkGlErrors");

        // Folder Info
        File gameDir = optionset.valueOf(optionGameDir);
        File assetsDir = optionset.has(optionAssetsDir) ? optionset.valueOf(optionAssetsDir) : new File(gameDir, "assets/");
        File resourcePackDir = optionset.has(optionResourcePackDir) ? optionset.valueOf(optionResourcePackDir) : new File(gameDir, "resourcepacks/");
        String assetIndex = optionset.has(optionAssetIndex) ? optionAssetIndex.value(optionset) : null;

        // Version Info
        boolean demo = optionset.has("demo");
        String version = optionset.valueOf(optionVersion);

        // Server Info
        String server = optionset.valueOf(optionServer);
        Integer port = optionset.valueOf(optionPort);

        GameConfiguration gameconfiguration = new GameConfiguration(
                new GameConfiguration.UserInformation(session, userProperties, profileProperties, proxy),
                new GameConfiguration.DisplayInformation(width, height, fullscreen, checkGlErrors),
                new GameConfiguration.FolderInformation(gameDir, resourcePackDir, assetsDir, assetIndex),
                new GameConfiguration.GameInformation(demo, version),
                new GameConfiguration.ServerInformation(server, port)
        );

        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
            public void run() {
                Minecraft.stopIntegratedServer();
            }
        });
        Thread.currentThread().setName("Client thread");
        new Minecraft(gameconfiguration).run();
    }

    private static boolean isNullOrEmpty(String str) {
        return str != null && !str.isEmpty();
    }
}
