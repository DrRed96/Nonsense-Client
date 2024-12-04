package wtf.bhopper.nonsense.module.property;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class Property<T> {

    public final String name;
    public final String displayName;
    public final String description;
    protected final Supplier<Boolean> dependency;
    private final List<ValueChangeListener<T>> valueChangeListeners = new ArrayList<>();
    protected T value;

    public Property(String displayName, String description, T value, Supplier<Boolean> dependency) {
        this.name = displayName.replace(' ', '-').toLowerCase();
        this.displayName = displayName;
        this.description = description;
        this.value = value;
        this.dependency = dependency;
    }

    public Property(String displayName, String description, T value) {
        this(displayName, description, value, () -> true);
    }

    public void addValueChangeListener(ValueChangeListener<T> valueChangeListener) {
        this.valueChangeListeners.add(valueChangeListener);
    }

    public boolean isAvailable() {
        return this.dependency.get();
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        T oldValue = this.value;
        this.value = value;
        if (oldValue != this.value) {
            for (ValueChangeListener<T> valueChangeListener : this.valueChangeListeners) {
                valueChangeListener.onValueChange(oldValue, value);
            }
        }
    }


    public abstract String getDisplayValue();

    public abstract void parseString(String str);

    public abstract JsonElement serialize();
    public abstract void deserialize(JsonElement element);

    public void callFirstTime() {
        for (ValueChangeListener<T> valueChangeListener : valueChangeListeners) {
            valueChangeListener.onValueChange(value, value);
        }
    }

    public Class<?> getType() {
        return value.getClass();
    }

}
