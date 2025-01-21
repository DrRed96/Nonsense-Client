package wtf.bhopper.nonsense.module.property.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import wtf.bhopper.nonsense.module.property.AbstractProperty;

import java.util.function.Supplier;

public class BooleanProperty extends AbstractProperty<Boolean> {

    public BooleanProperty(String displayName, String description, boolean value, Supplier<Boolean> dependency) {
        super(displayName, description, value, dependency);
    }

    public BooleanProperty(String displayName, String description, boolean value) {
        this(displayName, description, value, () -> true);
    }

    public void toggle() {
        this.set(!this.get());
    }

    @Override
    public String getDisplayValue() {
        return String.valueOf(this.get());
    }

    @Override
    public void parseString(String str) {
        this.set(str.equalsIgnoreCase("true") || str.equalsIgnoreCase("1") || str.equalsIgnoreCase("yes"));
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(this.get());
    }

    @Override
    public void deserialize(JsonElement element) {
        try {
            if (element.isJsonPrimitive()) {
                this.set(element.getAsBoolean());
            }
        } catch (Exception ignored) {}
    }

}
