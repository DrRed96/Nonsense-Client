package wtf.bhopper.nonsense.event.bus;

@FunctionalInterface
public interface Listener<Event> {
    void call(Event event);
}
