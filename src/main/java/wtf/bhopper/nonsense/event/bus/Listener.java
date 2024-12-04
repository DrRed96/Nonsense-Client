package wtf.bhopper.nonsense.event.bus;

@FunctionalInterface
public interface Listener<E> {
    void call(E event);
}
