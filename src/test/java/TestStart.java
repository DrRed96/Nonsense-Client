import net.minecraft.client.main.Main;
import net.minecraft.util.Util;

import java.util.Arrays;

public class TestStart {
    public static void main(String[] args) {
        Main.main(concat(new String[]{
                "--version", "Nonsense",
                "--accessToken", "0",
                "--gameDir", getMinecraftDirectory(),
                "--assetsDir", "assets",
                "--assetIndex", "1.8",
                "--userProperties", "{}",
                "--username", "Nonsense"
        }, args));
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static String getMinecraftDirectory() {
        return switch (Util.getOSType()) {
            case LINUX -> System.getenv("user.home") + "/.minecraft";
            case SOLARIS, UNKNOWN -> ".";
            case WINDOWS -> System.getenv("APPDATA") + "\\.minecraft";
            case OSX -> System.getenv("user.home") + "/Library/Application Support/minecraft";
        };
    }

}
