package wtf.bhopper.nonsense.util.misc;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

public class GeneralUtil {

    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    public static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    public static final String USERNAME = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_";
    public static final String VOWELS = "aeiou";
    public static final Pattern LINK_REGEX = Pattern.compile("^(?:(ftp|http|https)://)?(?:[\\w-]+\\.)+[a-z]{2,6}$");
    public static final String ASCII = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

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

    public static String paragraph(String... str) {
        return String.join("\n", str);
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static <T> T randomElement(Collection<T> collection) {
        return collection.stream()
                .skip(ThreadLocalRandom.current().nextInt(0, collection.size()))
                .findFirst()
                .orElse(null);
    }

    public static <T> T randomElement(T[] array) {
        if (array.length == 0) {
            throw new IllegalArgumentException("Array length cannot be 0");
        }

        return array[ThreadLocalRandom.current().nextInt(0, array.length)];
    }

    public static char randomElement(CharSequence str) {
        if (str.isEmpty()) {
            throw new IllegalArgumentException("Array length cannot be 0");
        }

        return str.charAt(ThreadLocalRandom.current().nextInt(0, str.length()));
    }

    public static String randomUsername(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(randomElement(USERNAME));
        }
        return builder.toString();
    }

    public static String randomString(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(randomElement(ASCII));
        }
        return builder.toString();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T nextValue(T val) {
        Class<T> clazz = (Class<T>)val.getClass();
        T[] vals = clazz.getEnumConstants();
        int ordinal = val.ordinal() + 1;
        if (ordinal >= vals.length) {
            return vals[0];
        }
        return vals[ordinal];
    }

}
