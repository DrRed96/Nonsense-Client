package wtf.bhopper.nonsense.module.property;

@FunctionalInterface
public interface ValueChangeListener<T> {
    void onValueChange(T oldValue, T value);
}
