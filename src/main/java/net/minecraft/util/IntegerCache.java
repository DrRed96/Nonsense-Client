package net.minecraft.util;

public class IntegerCache {
    private static final Integer[] cache = new Integer[0xffff];

    public static Integer func_181756_a(int p_181756_0_) {
        return p_181756_0_ > 0 && p_181756_0_ < cache.length ? cache[p_181756_0_] : Integer.valueOf(p_181756_0_);
    }

    static {
        int i = 0;

        for (int j = cache.length; i < j; ++i) {
            cache[i] = i;
        }
    }
}
