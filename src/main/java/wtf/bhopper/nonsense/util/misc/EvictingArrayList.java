package wtf.bhopper.nonsense.util.misc;

import java.util.ArrayList;

public class EvictingArrayList<E> extends ArrayList<E> {

    private final int maxSize;

    public EvictingArrayList(int maxSize) {
        super();
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(E e) {
        int size = this.size();
        if (size >= this.maxSize) {
            this.remove(0);
        }
        return super.add(e);
    }

    @Override
    public void add(int index, E element) {
        int size = this.size();
        if (size >= this.maxSize) {
            this.remove(0);
        }
        super.add(index, element);
    }

    public int getMaxSize() {
        return this.maxSize;
    }

}
