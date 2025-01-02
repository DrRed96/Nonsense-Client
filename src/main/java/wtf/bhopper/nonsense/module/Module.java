package wtf.bhopper.nonsense.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.module.property.Property;
import wtf.bhopper.nonsense.module.property.IPropertyContainer;
import wtf.bhopper.nonsense.util.minecraft.IMinecraft;
import wtf.bhopper.nonsense.util.misc.GeneralUtil;
import wtf.bhopper.nonsense.config.ISerializable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public abstract class Module implements IPropertyContainer, ISerializable, IMinecraft {

    public final String name = this.getClass().getAnnotation(ModuleInfo.class).name().replace(" ", "").toLowerCase();
    public final String displayName = this.getClass().getAnnotation(ModuleInfo.class).name();
    public final String description = this.getClass().getAnnotation(ModuleInfo.class).description();
    public final ModuleCategory category = this.getClass().getAnnotation(ModuleInfo.class).category();
    public final String[] searchAlias = GeneralUtil.concat(new String[]{this.displayName, this.description}, this.getClass().getAnnotation(ModuleInfo.class).searchAlias());

    private boolean toggled = this.getClass().getAnnotation(ModuleInfo.class).toggled();
    private int bind = this.getClass().getAnnotation(ModuleInfo.class).bind();
    private boolean hidden = this.getClass().getAnnotation(ModuleInfo.class).hidden() || category == ModuleCategory.VISUAL;
    private final List<Property<?>> properties = new ArrayList<>();

    private Supplier<String> suffix = () -> null;

    protected void autoAddProperties() {
        for (Field field : this.getClass().getDeclaredFields()) {

            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            if (!field.canAccess(this)) {
                field.setAccessible(true);
            }

            try {
                Object object = field.get(this);
                if (object instanceof Property<?> property) {
                    this.properties.add(property);
                }
            } catch (IllegalAccessException ignored) {}
        }
    }

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
        if (Hud.mod().toggleNotify.get()) {
            Notification.send("Toggle", (this.toggled ? "Enabled " : "Disabled ") + this.displayName, NotificationType.INFO, 3000);
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

    @Override
    public String getContainerId() {
        return this.name;
    }

    public Property<?> getProperty(String name) {
        return this.properties
                .stream()
                .filter(property -> property.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public boolean matches(String name) {

        if (name.isBlank()) {
            return true;
        }

        String nameLower = name.trim().toLowerCase();

        for (String search : this.searchAlias) {
            String search1 = search.toLowerCase();
            String search2 = search1.replace(" ", "");
            if (search1.contains(nameLower) || nameLower.contains(search1) || search2.contains(nameLower) || nameLower.contains(search2)) {
                return true;
            }
        }

        return false;
    }


    public void setSuffix(Supplier<String> suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return this.suffix.get();
    }

    @Override
    public JsonElement serialize() {
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

    @Override
    public void deserialize(JsonElement element) {

        if (!(element instanceof JsonObject moduleObject)) {
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
