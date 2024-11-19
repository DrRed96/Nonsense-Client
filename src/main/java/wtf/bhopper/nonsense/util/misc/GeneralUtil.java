package wtf.bhopper.nonsense.util.misc;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;

public class GeneralUtil {

    public static String capitalize(String str) {
        char prevChar = ' ';
        StringBuilder builder = new StringBuilder();

        for (char c : str.toCharArray()) {
            if (prevChar == ' ') {
                builder.append(Character.toTitleCase(c));
            } else {
                builder.append(c);
            }
            prevChar = c;
        }

        return builder.toString();
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static URL getResourcePath(ResourceLocation location) {
        return GeneralUtil.class.getResource("/assets/" + location.getResourceDomain() + "/" + location.getResourcePath());
    }

    public static String getResourcePathString(ResourceLocation location) {
        String path = URLDecoder.decode(getResourcePath(location).getFile(), StandardCharsets.UTF_8);
        if (Util.getOSType() == Util.EnumOS.WINDOWS && path.startsWith("/")) {
            return path.substring(1);
        }
        return path;
    }

}
