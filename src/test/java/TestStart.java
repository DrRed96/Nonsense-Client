import net.minecraft.client.main.Main;

import java.util.Arrays;

public class TestStart {
    public static void main(String[] args) {
        Main.main(concat(new String[]{
                "--version", "mcp",
                "--accessToken", "0",
                "--gameDir", System.getenv("appdata").replace('\\', '/') + "/.minecraft",
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
}
