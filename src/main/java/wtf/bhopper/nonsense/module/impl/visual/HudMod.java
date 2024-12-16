package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.screens.GuiMoveHudComponents;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.annotations.DisplayName;
import wtf.bhopper.nonsense.module.property.impl.*;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.Fonts;

@SuppressWarnings("FieldCanBeLocal")
@ModuleInfo(name = "HUD", description = "Heads Up Display.", category = ModuleCategory.VISUAL, toggled = true)
public class HudMod extends Module {

    private final GroupProperty moduleListGroup = new GroupProperty("Module List", "Displays what modules are enabled", this);
    public final BooleanProperty moduleListEnabled = new BooleanProperty("Enable", "Enable the module list.", true);
    public final EnumProperty<ModuleListMode> moduleListMode = new EnumProperty<>("Mode", "Style of the module list", ModuleListMode.EXHIBITION);
    public final EnumProperty<ModuleListColor> moduleListColor = new EnumProperty<>("Color", "Color mode for the module list.", ModuleListColor.STATIC, () -> moduleListMode.is(ModuleListMode.EXHIBITION));
    public final BooleanProperty moduleListDisplay = new BooleanProperty("Display Names", "Uses display names.", true);
    public final EnumProperty<ModuleListSuffix> moduleListSuffix = new EnumProperty<>("Suffixes", "Displays module suffixes", ModuleListSuffix.NORMAL, () -> moduleListMode.is(ModuleListMode.EXHIBITION));
    public final ColorProperty moduleListSuffixColor = new ColorProperty("Suffix Color", "Color for the suffixes", ColorUtil.GRAY, () -> moduleListMode.is(ModuleListMode.EXHIBITION) && !this.moduleListSuffix.is(ModuleListSuffix.NONE));
    public final BooleanProperty moduleListLowerCase = new BooleanProperty("Lower Case", "Converts the module list to lower case.", false);
    public final BooleanProperty moduleListAnimated = new BooleanProperty("Animated", "Animate the module list.", true);
    public final NumberProperty moduleListSpacing = new NumberProperty("Spacing", "Space between modules", () -> moduleListMode.is(ModuleListMode.EXHIBITION), 1.0, 0.0, 2.0, 1.0, NumberProperty.FORMAT_INT);
    public final NumberProperty moduleListBackground = new NumberProperty("Background", "Background transparency.", () -> moduleListMode.is(ModuleListMode.EXHIBITION), 120.0, 0.0, 255.0, 1.0, NumberProperty.FORMAT_INT);
    public final EnumProperty<ModuleListOutline> moduleListOutline = new EnumProperty<>("Outline", "Outline the module list", ModuleListOutline.NONE);
    public final EnumProperty<ModuleListSorting> moduleListSorting = new EnumProperty<>("Sorting", "How the module list should be sorted", ModuleListSorting.LENGTH);

    private final GroupProperty watermarkGroup = new GroupProperty("Watermark", "Client Watermark", this);
    public final BooleanProperty watermarkEnabled = new BooleanProperty("Enabled", "Enables the water mark", true);
    public final EnumProperty<WatermarkMode> watermarkMode = new EnumProperty<>("Mode", "Watermark mode.", WatermarkMode.EXHIBITION);
    public final EnumProperty<WatermarkColorMode> watermarkColorMode = new EnumProperty<>("Color Mode", "Watermark color mode.", WatermarkColorMode.STATIC);
    public final StringProperty watermarkText = new StringProperty("Text", "Oh look mom, I can rename a client!", Nonsense.NAME);

    private final GroupProperty information = new GroupProperty("Information", "HUD Information", this);
    public final BooleanProperty armorHud = new BooleanProperty("Armor HUD", "Displays your armor above your hotbar", true);
    public final BooleanProperty coords = new BooleanProperty("Coordinates", "Displays your coordinates.", true);
    public final BooleanProperty angles = new BooleanProperty("Angles", "Displays your pitch and yaw.", false);
    public final EnumProperty<Speed> speed = new EnumProperty<>("Speed", "Display your move speed.", Speed.MPS);
    public final BooleanProperty tps = new BooleanProperty("TPS", "Displays the servers ticks per second.", false);
    public final BooleanProperty pots = new BooleanProperty("Potions", "Displays your active potion effects.", true);
    public final BooleanProperty fps = new BooleanProperty("FPS", "Displays your FPS.", false);

    private final GroupProperty notificationGroup = new GroupProperty("Notifications", "Notifications", this);
    public final BooleanProperty notificationEnabled = new BooleanProperty("Enabled", "Enables notifications.", true);
    public final EnumProperty<NotificationSound> notificationSound = new EnumProperty<>("Sound", "Sound that gets played when you get a notification.", NotificationSound.POP);
    public final BooleanProperty toggleNotify = new BooleanProperty("Toggle Notification", "Sends a notification when you toggle a module.", false);

    public final GroupProperty targetHudGroup = new GroupProperty("Target HUD", "Displays your current target.", this);
    public final BooleanProperty targetHudEnabled = new BooleanProperty("Enabled", "Enables Target HUD", true);
    public final EnumProperty<TargetHud> targetHudMode = new EnumProperty<>("Mode", "Target HUD mode", TargetHud.ASTOLFO);
    public final EnumProperty<TargetColor> targetHudColorMode = new EnumProperty<>("Color Mode", "Target HUD color mode", TargetColor.STATIC);

    public final ColorProperty color = new ColorProperty("Color", "HUD color.", ColorUtil.NONSENSE);
    public final EnumProperty<Font> font = new EnumProperty<>("Font", "Which font to use for the HUD", Font.ARIAL);
    public final NumberProperty fontSize = new NumberProperty("Font Size", "Size of the custom font", () -> !this.font.is(Font.MINECRAFT), 18.0, 11.0, 24.0, 1.0, NumberProperty.FORMAT_PIXELS);
    public final ButtonProperty moveComponents = new ButtonProperty("Move Components", "Click to move the HUD components.", () -> mc.displayGuiScreen(new GuiMoveHudComponents()));
    public final BooleanProperty hideInF3 = new BooleanProperty("Hide In F3", "Hides the HUD while in F3", true);
    
    public HudMod() {
        this.moduleListGroup.addProperties(this.moduleListEnabled,
                this.moduleListMode,
                this.moduleListColor,
                this.moduleListDisplay,
                this.moduleListSuffix,
                this.moduleListSuffixColor,
                this.moduleListLowerCase,
                this.moduleListAnimated,
                this.moduleListSpacing,
                this.moduleListBackground,
                this.moduleListOutline,
                this.moduleListSorting);
        this.watermarkGroup.addProperties(this.watermarkEnabled, this.watermarkMode, this.watermarkColorMode, this.watermarkText);
        this.information.addProperties(this.armorHud, this.coords, this.angles, this.speed, this.tps, this.pots, this.fps);
        this.notificationGroup.addProperties(this.notificationEnabled, this.notificationSound, this.toggleNotify);
        this.targetHudGroup.addProperties(this.targetHudEnabled, this.targetHudMode, this.targetHudColorMode);
        this.addProperties(this.moduleListGroup, this.watermarkGroup, this.information, this.notificationGroup, this.targetHudGroup, this.color, this.font, this.fontSize, this.moveComponents, this.hideInF3);
    }

    public enum ModuleListMode {
        EXHIBITION
    }

    public enum ModuleListColor {
        STATIC,
        WAVY,
        RAINBOW,
        RAINBOW_2,
        EXHIBITION_RAINBOW,
        CATEGORY,
        ASTOLFO,
        RANDOM,
        TRANS
    }

    public enum ModuleListSuffix {
        NORMAL,
        HYPHEN,
        BRACKET,
        SQUARE,
        ANGLE,
        CURLY,
        NONE
    }

    public enum ModuleListOutline {
        LEFT,
        RIGHT,
        FULL,
        NONE
    }

    public enum ModuleListSorting {
        LENGTH,
        ALPHABETICAL
    }

    public enum WatermarkMode {
        EXHIBITION
    }

    public enum WatermarkColorMode {
        WHITE,
        STATIC,
        BREATHING,
        SOLID,
        RAINBOW,
        RAINBOW_2
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

    public enum TargetHud {
        DETAILED,
        ASTOLFO,
        RAVEN,
    }

    public enum TargetColor {
        STATIC,
        HEALTH
    }

    public enum Font {
        ARIAL(Fonts.ARIAL),
        CASCADIA_MONO(Fonts.CASCADIA_MONO),
        COMIC_SANS(Fonts.COMIC_SANS),
        CONSOLAS(Fonts.CONSOLAS),
        HELVETICA(Fonts.HELVETICA),
        @DisplayName("JetBrains Mono") JETBRAINS_MONO(Fonts.JETBRAINS_MONO),
        OUTFIT(Fonts.OUTFIT),
        ROBOTO(Fonts.ROBOTO),
        SANS_SERIF(Fonts.SANS_SERIF),
        SEGOE(Fonts.SEGOE),
        @DisplayName("SF Pro Rounded") SF_PRO_ROUNDED(Fonts.SF_PRO_ROUNDED),
        TAHOMA(Fonts.TAHOMA),
        TIMES_NEW_ROMAN(Fonts.TIMES_NEW_ROMAN),
        MINECRAFT(Fonts.ARIAL); // Will default to arial if Minecraft font is not implemented

        public final Fonts font;

        Font(Fonts font) {
            this.font = font;
        }
    }

}
