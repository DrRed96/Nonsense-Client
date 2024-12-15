package wtf.bhopper.nonsense.module.property.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import imgui.ImGui;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.impl.visual.ClickGui;
import wtf.bhopper.nonsense.module.property.Property;
import wtf.bhopper.nonsense.module.property.annotations.Description;
import wtf.bhopper.nonsense.module.property.annotations.DisplayName;
import wtf.bhopper.nonsense.util.misc.GeneralUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class EnumProperty<T extends Enum<T>> extends Property<T> {

    private final T[] values;

    public EnumProperty(String displayName, String description, T value, Supplier<Boolean> dependency) {
        super(displayName, description, value, dependency);
        this.values = this.getEnumConstants();
    }

    public EnumProperty(String displayName, String description, T value) {
        this(displayName, description, value, () -> true);
    }

    @SuppressWarnings("unchecked")
    private T[] getEnumConstants() {
        return (T[])this.get().getClass().getEnumConstants();
    }

    public boolean is(T value) {
        return this.get() == value;
    }

    public boolean isAny(T... values) {
        for (T value : values) {
            if (this.get() == value) return true;
        }
        return false;
    }

    public void cycleForwards() {
        int index = this.get().ordinal() + 1;
        if (index >= values.length) index = 0;
        this.set(values[index]);
    }

    public void cycleBackwards() {
        int index = this.get().ordinal() - 1;
        if (index < 0) index = values.length - 1;
        this.set(values[index]);
    }

    public void setFromOrdinal(int ordinal) {
        for (T value : this.values) {
            if (value.ordinal() == ordinal) {
                this.set(value);
                return;
            }
        }
    }

    public String getFullDescription() {
        String enumDesc = getEnumDescription(this.get());
        if (enumDesc != null) {
            return this.description + "\n" + this.getDisplayValue() + ": " + enumDesc;
        }
        return this.description;
    }

    public Map<T, String> valueNameMap() {
        Map<T, String> map = new HashMap<>();
        for (T t : values) {
            map.put(t, toDisplay(t));
        }
        return map;
    }

    @Override
    public String getDisplayValue() {
        return toDisplay(this.get());
    }

    @Override
    public void parseString(String str) {
        for (T value : this.values) {
            if (value.name().equalsIgnoreCase(str)) {
                this.set(value);
                break;
            }
        }
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(this.get().name());
    }

    @Override
    public void deserialize(JsonElement element) {
        try {
            String valueStr = element.getAsString();
            for (T value : this.values) {
                if (value.name().equalsIgnoreCase(valueStr)) {
                    this.set(value);
                    break;
                }
            }
        } catch (Exception ignored) {}
    }

    public static <E extends Enum<?>> String toDisplay(E e) {

        try {
            if (e.getClass().getField(e.name()).isAnnotationPresent(DisplayName.class)) {
                return e.getClass().getField(e.name()).getAnnotation(DisplayName.class).value();
            }
        } catch (NoSuchFieldException | NullPointerException ignored) {}

        return toDisplay(e.name());
    }

    public static String toDisplay(String str) {
        return GeneralUtil.capitalize(str.replace('_', ' ').toLowerCase());
    }

    public static <E extends Enum<E>> String getEnumDescription(E e) {
        try {
            if (e.getClass().getField(e.name()).isAnnotationPresent(Description.class))  {
                return e.getClass().getField(e.name()).getAnnotation(Description.class).value();
            }
        } catch (NoSuchFieldException | NullPointerException ignored) {}

        return null;
    }

    public void imGuiDraw() {
        // An internal method is needed here to manage the generics

        Map<T, String> nameMap = this.valueNameMap();

        if (ImGui.beginCombo(this.displayName, this.getDisplayValue())) {
            for (T value : values) {

                if (ImGui.selectable(nameMap.get(value))) {
                    this.set(value);
                }
                String desc = getEnumDescription(value);
                if (desc != null) {
                    if (ImGui.isItemHovered() && Nonsense.module(ClickGui.class).toolTips.get()) {
                        ImGui.setTooltip(desc);
                    }
                }
            }
            ImGui.endCombo();
        } else if (ImGui.isItemHovered() && Nonsense.module(ClickGui.class).toolTips.get()) {
            ImGui.setTooltip(this.description);
        }

    }
}
