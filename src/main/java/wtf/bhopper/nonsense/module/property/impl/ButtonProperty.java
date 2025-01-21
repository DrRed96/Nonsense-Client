package wtf.bhopper.nonsense.module.property.impl;

import com.google.gson.JsonElement;
import wtf.bhopper.nonsense.module.property.AbstractProperty;

import java.util.function.Supplier;

public class ButtonProperty extends AbstractProperty<Void> {

    private final Runnable runnable;

    public ButtonProperty(String displayName, String description, Runnable runnable, Supplier<Boolean> dependency) {
        super(displayName, description, null, dependency);
        this.runnable = runnable;
    }

    public ButtonProperty(String displayName, String description, Runnable runnable) {
        this(displayName, description, runnable, () -> true);
    }

    public void execute() {
        this.runnable.run();
    }

    @Override
    public String getDisplayValue() {
        return this.displayName;
    }

    @Override
    public void parseString(String str) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonElement serialize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deserialize(JsonElement element) {
        throw new UnsupportedOperationException();
    }
}
