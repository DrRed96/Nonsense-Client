package wtf.bhopper.nonsense.module.property.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.module.property.Property;
import wtf.bhopper.nonsense.module.property.PropertyContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class GroupProperty extends Property<List<Property<?>>> implements PropertyContainer {

    public GroupProperty(String displayName, String description, Supplier<Boolean> dependency) {
        super(displayName, description, new ArrayList<>(), dependency);
    }

    public GroupProperty(String displayName, String description) {
        this(displayName, description, () -> true);
    }

    @Override
    public String getDisplayValue() {
        return "[Group]";
    }

    @Override
    public JsonElement serialize() {
        JsonObject object = new JsonObject();
        for (Property<?> property : this.getProperties()) {
            try {
                object.add(property.name, property.serialize());
            } catch (UnsupportedOperationException ignored) {}
        }
        return object;
    }

    @Override
    public void deserialize(JsonElement element) {
        if (element instanceof JsonObject object) {
            for (Property<?> property : this.getProperties()) {
                if (object.has(property.name)) {
                    try {
                        property.deserialize(object.get(property.name));
                    } catch (UnsupportedOperationException ignored) {}

                }
            }
        }
    }

    @Override
    public void addProperties(Property<?>... properties) {
        this.value.addAll(Arrays.asList(properties));
    }

    @Override
    public List<Property<?>> getProperties() {
        return this.value;
    }
}
