package wtf.bhopper.nonsense.module.property.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import wtf.bhopper.nonsense.module.property.AbstractProperty;

import java.util.function.Supplier;

public class StringProperty extends AbstractProperty<String> {

    public StringProperty(String displayName, String description, String value, Supplier<Boolean> dependency) {
        super(displayName, description, value, dependency);
    }

    public StringProperty(String displayName, String description, String value) {
        this(displayName, description, value, () -> true);
    }

    public void append(String str) {
        this.set(this.get() + str);
    }

    public void append(char c) {
        this.set(this.get() + c);
    }

    public void backspace() {
        if (!this.isEmpty()) {
            this.set(this.get().substring(0, this.length() - 1));
        }
    }

    public int length() {
        return this.get().length();
    }

    public boolean isEmpty() {
        return this.get().isEmpty();
    }

    @Override
    public String getDisplayValue() {
        return this.get();
    }

    @Override
    public void parseString(String str) {
        this.set(str);
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(this.get());
    }

    @Override
    public void deserialize(JsonElement element) {
        try {
            this.set(element.getAsString());
        } catch (Exception ignored) {}
    }
}
