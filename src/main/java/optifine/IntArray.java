package optifine;

public class IntArray
{
    private final int[] array;
    private int position = 0;
    private int limit = 0;

    public IntArray(int size)
    {
        this.array = new int[size];
    }

    public void put(int value)
    {
        this.array[this.position] = value;
        ++this.position;

        if (this.limit < this.position)
        {
            this.limit = this.position;
        }
    }

    public void put(int index, int value)
    {
        this.array[index] = value;

        if (this.limit < index)
        {
            this.limit = index;
        }
    }

    public void position(int position)
    {
        this.position = position;
    }

    public void put(int[] array)
    {
        int i = array.length;

        for (int k : array) {
            this.array[this.position] = k;
            ++this.position;
        }

        if (this.limit < this.position)
        {
            this.limit = this.position;
        }
    }

    public int get(int p_get_1_)
    {
        return this.array[p_get_1_];
    }

    public int[] getArray()
    {
        return this.array;
    }

    public void clear()
    {
        this.position = 0;
        this.limit = 0;
    }

    public int getLimit()
    {
        return this.limit;
    }

    public int getPosition()
    {
        return this.position;
    }
}
