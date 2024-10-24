package wtf.bhopper.nonsense.module.impl.visual;

import org.lwjgl.input.Keyboard;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.clickgui.dropdown.DropdownClickGui;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;
import wtf.bhopper.nonsense.module.setting.impl.ColorSetting;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.module.setting.util.Description;
import wtf.bhopper.nonsense.module.setting.util.DisplayName;

import java.awt.*;

public class ClickGuiMod extends Module {

    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "Mode", Mode.DROPDOWN);
    public final EnumSetting<ColorMode> colorMode = new EnumSetting<>("Color Mode", "Color Mode", ColorMode.STATIC);
    public final ColorSetting color = new ColorSetting("Color", "Click GUI color", new Color(0xFFFF5555));
    public final BooleanSetting toolTips = new BooleanSetting("Tool Tips", "Enables tool tips", true);
    public final BooleanSetting keyBinds = new BooleanSetting("Key Binds", "Displays key binds next to module names", true);
    public final BooleanSetting displayNames = new BooleanSetting("Display Names", "Use display names", true);
    public final EnumSetting<Background> background = new EnumSetting<>("Background", "Click GUI background", Background.NONE);
    public final BooleanSetting showHidden = new BooleanSetting("Hidden Settings", "Shows hidden settings", false);

    public ClickGuiMod() {
        super("Click GUI", "Click GUI properties", Category.VISUAL);
        this.setBind(Keyboard.KEY_RSHIFT);
        this.addSettings(mode, colorMode, color, toolTips, keyBinds, displayNames, background, showHidden);
    }

    @Override
    public void onEnable() {
        this.toggle(false);
        Nonsense.INSTANCE.eventBus.unsubscribe(this);
        if (mc.currentScreen == null) {
            switch (this.mode.get()) {
                case DROPDOWN:
                    mc.displayGuiScreen(new DropdownClickGui());
                    break;

            }
        }
    }

    public enum Mode {
        DROPDOWN,
    }

    public enum ColorMode {
        STATIC,
        @Description("test") CATEGORY,
        WAVY,
        RAINBOW_1,
        RAINBOW_2
    }

    public enum Background {
        DARKEN,
        NONE
    }
}
