package wtf.bhopper.nonsense.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.property.Property;
import wtf.bhopper.nonsense.module.property.PropertyContainer;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public abstract class Module implements PropertyContainer, MinecraftInstance {

    public final String name = this.getClass().getAnnotation(ModuleInfo.class).name().replace(" ", "").toLowerCase();
    public final String displayName = this.getClass().getAnnotation(ModuleInfo.class).name();
    public final String description = this.getClass().getAnnotation(ModuleInfo.class).description();
    public final ModuleCategory category = this.getClass().getAnnotation(ModuleInfo.class).category();

    private boolean toggled = false;
    private int bind = this.getClass().getAnnotation(ModuleInfo.class).bind();
    private boolean hidden = category == ModuleCategory.VISUAL;
    private final List<Property<?>> properties = new ArrayList<>();

    private Supplier<String> suffix = () -> null;

    public void toggle(boolean toggled) {
        if (this.toggled == toggled) {
            // Avoid triggering onEnable/onDisable when not needed
            return;
        }

        this.toggled = toggled;
        if (this.toggled) {
            Nonsense.getEventBus().subscribe(this);
            this.onEnable();
        } else {
            Nonsense.getEventBus().unsubscribe(this);
            this.onDisable();
        }
    }

    public void toggle() {
        this.toggle(!this.toggled);
    }

    public boolean isToggled() {
        return this.toggled;
    }

    public void setBind(int bind) {
        this.bind = bind;
    }

    public int getBind() {
        return this.bind;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    @Override
    public void addProperties(Property<?>... properties) {
        this.properties.addAll(Arrays.asList(properties));
    }

    @Override
    public List<Property<?>> getProperties() {
        return this.properties;
    }

    public void setSuffix(Supplier<String> suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return this.suffix.get();
    }

    public JsonObject serialize() {
        JsonObject moduleObject = new JsonObject();
        moduleObject.addProperty("toggled", this.toggled);
        moduleObject.addProperty("bind", this.bind);
        moduleObject.addProperty("hidden", this.hidden);

        JsonObject properties = new JsonObject();
        for (Property<?> property : this.properties) {
            try {
                properties.add(property.name, property.serialize());
            } catch (UnsupportedOperationException ignored) {}
        }
        moduleObject.add("properties", properties);

        return moduleObject;
    }

    public void deserialize(JsonObject moduleObject) {

        if (moduleObject == null) {
            return;
        }

        if (moduleObject.has("toggled")) {
            this.toggle(moduleObject.get("toggled").getAsBoolean());
        }

        if (moduleObject.has("bind")) {
            this.bind = moduleObject.get("bind").getAsInt();
        }

        if (moduleObject.has("hidden")) {
            this.hidden = moduleObject.get("hidden").getAsBoolean();
        }

        if (moduleObject.has("properties")) {
            JsonElement propertiesElement = moduleObject.get("properties");
            if (propertiesElement instanceof JsonObject object) {
                for (Property<?> property : this.properties) {
                    if (object.has(property.name)) {
                        try {
                            property.deserialize(object.get(property.name));
                        } catch (UnsupportedOperationException ignored) {}
                    }
                }
            }
        }


    }

    public void onEnable() {}
    public void onDisable() {}

}
