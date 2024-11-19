package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
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

    private final GroupProperty notificationGroup = new GroupProperty("Notifications", "Notifications");
    public final BooleanProperty notificationEnabled = new BooleanProperty("Enabled", "Enables notifications.", true);
    public final EnumProperty<NotificationSound> notificationSound = new EnumProperty<>("Sound", "Sound that gets played when you get a notification.", NotificationSound.POP);

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
        this.notificationGroup.addProperties(this.notificationEnabled, this.notificationSound);
        this.addProperties(this.moduleListGroup, this.notificationGroup);
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
