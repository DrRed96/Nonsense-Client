package wtf.bhopper.nonsense.module.property;

import com.google.common.base.Objects;
import wtf.bhopper.nonsense.config.ISerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractProperty<T> implements ISerializable {

    public final String name;
    public final String displayName;
    public final String description;
    protected final Supplier<Boolean> dependency;
    private final List<IValueChangeListener<T>> valueChangeListeners = new ArrayList<>();
    protected T value;

    public AbstractProperty(String displayName, String description, T value, Supplier<Boolean> dependency) {
        this.name = displayName.replace(' ', '-').toLowerCase();
        this.displayName = displayName;
        this.description = description;
        this.value = value;
        this.dependency = dependency;
    }

    public AbstractProperty(String displayName, String description, T value) {
        this(displayName, description, value, () -> true);
    }

    public void addValueChangeListener(IValueChangeListener<T> valueChangeListener) {
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
            for (IValueChangeListener<T> valueChangeListener : this.valueChangeListeners) {
                valueChangeListener.onValueChange(oldValue, value);
            }
        }
    }

    public abstract String getDisplayValue();

    public abstract void parseString(String str);

    public void callFirstTime() {
        for (IValueChangeListener<T> valueChangeListener : this.valueChangeListeners) {
            valueChangeListener.onValueChange(this.value, this.value);
        }
    }

    public Class<?> getType() {
        return this.value.getClass();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", this.name)
                .add("value", this.value)
                .toString();
    }
}
