package wtf.bhopper.nonsense.event;

@FunctionalInterface
public interface Listener<E> {
    void call(E event);
}
