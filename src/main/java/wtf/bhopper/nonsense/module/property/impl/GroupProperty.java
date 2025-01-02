package wtf.bhopper.nonsense.module.property.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.module.property.Property;
import wtf.bhopper.nonsense.module.property.IPropertyContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class GroupProperty extends Property<List<Property<?>>> implements IPropertyContainer {

    private final IPropertyContainer owner;

    public GroupProperty(String displayName, String description, IPropertyContainer owner, Supplier<Boolean> dependency) {
        super(displayName, description, new ArrayList<>(), dependency);
        this.owner = owner;
    }

    public GroupProperty(String displayName, String description, IPropertyContainer owner) {
        this(displayName, description, owner, () -> true);
    }

    @Override
    public String getDisplayValue() {
        return "[Group]";
    }

    @Override
    public void parseString(String str) {
        throw new UnsupportedOperationException();
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

    @Override
    public String getContainerId() {
        List<String> id = new ArrayList<>(Collections.singletonList(this.name));
        IPropertyContainer owner = this.getOwner();
        while (owner != null) {
            id.add(owner.getContainerId());
            owner = owner.getOwner();
        }
        return String.join(":", id);
    }

    @Override
    public IPropertyContainer getOwner() {
        return this.owner;
    }

    public Property<?> getProperty(String name) {
        return this.get()
                .stream()
                .filter(property -> property.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
