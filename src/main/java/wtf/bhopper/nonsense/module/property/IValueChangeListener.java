package wtf.bhopper.nonsense.module.property;

@FunctionalInterface
public interface IValueChangeListener<T> {
    void onValueChange(T oldValue, T value);
}
