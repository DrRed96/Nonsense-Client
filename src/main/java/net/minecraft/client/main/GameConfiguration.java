package net.minecraft.client.main;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import net.minecraft.util.Session;

public class GameConfiguration
{
    public final GameConfiguration.UserInformation userInfo;
    public final GameConfiguration.DisplayInformation displayInfo;
    public final GameConfiguration.FolderInformation folderInfo;
    public final GameConfiguration.GameInformation gameInfo;
    public final GameConfiguration.ServerInformation serverInfo;

    public GameConfiguration(GameConfiguration.UserInformation userInfo, GameConfiguration.DisplayInformation displayInfo, GameConfiguration.FolderInformation folderInfo, GameConfiguration.GameInformation gameInfo, GameConfiguration.ServerInformation serverInfo)
    {
        this.userInfo = userInfo;
        this.displayInfo = displayInfo;
        this.folderInfo = folderInfo;
        this.gameInfo = gameInfo;
        this.serverInfo = serverInfo;
    }

    public static class DisplayInformation
    {
        public final int width;
        public final int height;
        public final boolean fullscreen;
        public final boolean checkGlErrors;

        public DisplayInformation(int width, int height, boolean fullscreen, boolean checkGlErrors)
        {
            this.width = width;
            this.height = height;
            this.fullscreen = fullscreen;
            this.checkGlErrors = checkGlErrors;
        }
    }

    public static class FolderInformation
    {
        public final File mcDataDir;
        public final File resourcePacksDir;
        public final File assetsDir;
        public final String assetIndex;

        public FolderInformation(File mcDataDir, File resourcePacksDir, File assetsDir, String assetIndex)
        {
            this.mcDataDir = mcDataDir;
            this.resourcePacksDir = resourcePacksDir;
            this.assetsDir = assetsDir;
            this.assetIndex = assetIndex;
        }
    }

    public static class GameInformation
    {
        public final boolean isDemo;
        public final String version;

        public GameInformation(boolean isDemo, String version)
        {
            this.isDemo = isDemo;
            this.version = version;
        }
    }

    public static class ServerInformation
    {
        public final String serverName;
        public final int serverPort;

        public ServerInformation(String serverName, int serverPort)
        {
            this.serverName = serverName;
            this.serverPort = serverPort;
        }
    }

    public static class UserInformation
    {
        public final Session session;
        public final PropertyMap userProperties;
        public final PropertyMap profileProperties;
        public final Proxy proxy;

        public UserInformation(Session session, PropertyMap userProperties, PropertyMap profileProperties, Proxy proxy)
        {
            this.session = session;
            this.userProperties = userProperties;
            this.profileProperties = profileProperties;
            this.proxy = proxy;
        }
    }
}
