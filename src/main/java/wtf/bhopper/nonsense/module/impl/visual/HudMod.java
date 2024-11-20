package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.annotations.DisplayName;
import wtf.bhopper.nonsense.module.property.impl.*;
import wtf.bhopper.nonsense.util.render.ColorUtil;

@ModuleInfo(name = "HUD", description = "Heads Up Display.", category = ModuleCategory.VISUAL)
public class HudMod extends Module {

    private final GroupProperty moduleListGroup = new GroupProperty("Module List", "Displays what modules are enabled");
    public final BooleanProperty moduleListEnabled = new BooleanProperty("Enable", "Enable the module list.", true);
    public final EnumProperty<ModuleListColor> moduleListColorMode = new EnumProperty<>("Color Mode", "Color mode for the module list.", ModuleListColor.STATIC);
    public final ColorProperty moduleListColor = new ColorProperty("Color", "Color of the module list.", ColorUtil.NONSENSE_COLOR, () -> this.moduleListColorMode.isAny(ModuleListColor.STATIC, ModuleListColor.WAVY));
    public final BooleanProperty moduleListDisplay = new BooleanProperty("Display Names", "Uses display names.", true);
    public final BooleanProperty moduleListLowerCase = new BooleanProperty("Lower Case", "Converts the module list to lower case.", false);
    public final NumberProperty moduleListFontSize = new NumberProperty("Font Size", "Font size for the module list", 18.0, 11.0, 24.0, 1.0, NumberProperty.FORMAT_PIXELS);
    public final BooleanProperty moduleListAnimated = new BooleanProperty("Animated", "Animate the module list.", true);
    public final NumberProperty moduleListSpacing = new NumberProperty("Spacing", "Space between modules", 1.0, 0.0, 2.0, 1.0, NumberProperty.FORMAT_INT);
    public final NumberProperty moduleListBackground = new NumberProperty("Background", "Background transparency.", 120.0, 0.0, 255.0, 1.0, NumberProperty.FORMAT_INT);
    public final EnumProperty<ModuleListSorting> moduleListSorting = new EnumProperty<>("Sorting", "How the module list should be sorted", ModuleListSorting.LENGTH);

    private final GroupProperty information = new GroupProperty("Information", "HUD Information");
    public final BooleanProperty coords = new BooleanProperty("Coordinates", "Displays your coordinates.", true);
    public final BooleanProperty angles = new BooleanProperty("Angles", "Displays your pitch and yaw.", false);
    public final EnumProperty<Speed> speed = new EnumProperty<>("Speed", "Display your move speed.", Speed.MPS);
    public final BooleanProperty tps = new BooleanProperty("TPS", "Displays the servers ticks per second.", false);
    public final BooleanProperty pots = new BooleanProperty("Potions", "Displays your active potion effects.", true);
    public final BooleanProperty fps = new BooleanProperty("FPS", "Displays your FPS.", false);

    private final GroupProperty notificationGroup = new GroupProperty("Notifications", "Notifications");
    public final BooleanProperty notificationEnabled = new BooleanProperty("Enabled", "Enables notifications.", true);
    public final EnumProperty<NotificationSound> notificationSound = new EnumProperty<>("Sound", "Sound that gets played when you get a notification.", NotificationSound.POP);

    public final ColorProperty color = new ColorProperty("Color", "HUD color.", ColorUtil.NONSENSE_COLOR);
    public final NumberProperty fontSize = new NumberProperty("Font Size", "Size of the custom font", 18.0, 11.0, 24.0, 1.0, NumberProperty.FORMAT_PIXELS);
    public final BooleanProperty hideInF3 = new BooleanProperty("Hide In F3", "Hides the HUD while in F3", true);

    public HudMod() {
        this.moduleListGroup.addProperties(this.moduleListEnabled,
                this.moduleListColorMode,
                this.moduleListColor,
                this.moduleListDisplay,
                this.moduleListLowerCase,
                this.moduleListFontSize,
                this.moduleListAnimated,
                this.moduleListSpacing,
                this.moduleListBackground,
                this.moduleListSorting);
        this.information.addProperties(this.coords, this.angles, this.speed, this.tps, this.pots, this.fps);
        this.notificationGroup.addProperties(this.notificationEnabled, this.notificationSound);
        this.addProperties(this.moduleListGroup, this.information, this.notificationGroup, this.color, this.fontSize, this.hideInF3);
        this.toggle(true);
    }

    public enum ModuleListColor {
        STATIC,
        WAVY,
        RAINBOW,
        RAINBOW_2,
        RAINBOW_3,
        CATEGORY,
        ASTOLFO,
        RANDOM,
        TRANS
    }

    public enum ModuleListSorting {
        LENGTH,
        ALPHABETICAL
    }

    public enum Speed {
        @DisplayName("m/s") MPS,
        @DisplayName("Km/h") KMPH,
        @DisplayName("mph") MPH,
        RAW,
        NONE
    }

    public enum NotificationSound {
        POP("random.pop", 1),
        DING("random.orb", 1),
        NONE("", 0);

        private final ResourceLocation sound;
        private final int pitch;

        NotificationSound(String sound, int pitch) {
            this.sound = new ResourceLocation(sound);
            this.pitch = pitch;
        }

        public ISound createSoundRecord()  {
            return PositionedSoundRecord.create(this.sound, this.pitch);
        }
    }

}
