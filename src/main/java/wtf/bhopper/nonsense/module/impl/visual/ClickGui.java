package wtf.bhopper.nonsense.module.impl.visual;

import org.lwjglx.input.Keyboard;
import wtf.bhopper.nonsense.gui.click.novoline.NovoGui;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.ColorProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.util.minecraft.ChatUtil;
import wtf.bhopper.nonsense.util.render.ColorUtil;

import java.util.function.Supplier;

@ModuleInfo(name = "Click GUI",
        description = "Configure the Click GUI.",
        category = ModuleCategory.VISUAL,
        bind = Keyboard.KEY_RSHIFT)
public class ClickGui extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Which Click GUI to use.", Mode.NOVOLINE);
    public final BooleanProperty categoryColors = new BooleanProperty("Category Colors", "Category colors.", false);
    public final ColorProperty color = new ColorProperty("Color", "Color of the Click GUI", ColorUtil.NONSENSE_COLOR, () -> !this.categoryColors.get());
    public final BooleanProperty toolTips = new BooleanProperty("Toop Tips", "Renders tool tips.", false);

    private NovoGui novoline;

    public ClickGui() {
        this.addProperties(this.mode, this.categoryColors, this.color, this.toolTips);
    }

    public void initGuis() {
        novoline = new NovoGui();
    }

    @Override
    public void onEnable() {
        this.toggle(false);
        if (mc.currentScreen == null) {

            switch (this.mode.get()) {
                case NOVOLINE -> mc.displayGuiScreen(novoline);
                default -> ChatUtil.error("Someone has been tampering with the Click GUI's!");
            }
        }
    }

    private enum Mode {
        NOVOLINE
    }

}
