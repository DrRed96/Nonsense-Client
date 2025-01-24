package optifine;

public class IntegerCache
{
    private static final int CACHE_SIZE = 4096;
    private static final Integer[] cache = makeCache(CACHE_SIZE);

    private static Integer[] makeCache(int size)
    {
        Integer[] ainteger = new Integer[size];

        for (int i = 0; i < size; ++i)
        {
            ainteger[i] = i;
        }

        return ainteger;
    }

    public static Integer valueOf(int size)
    {
        return size >= 0 && size < CACHE_SIZE ? cache[size] : Integer.valueOf(size);
    }
}
