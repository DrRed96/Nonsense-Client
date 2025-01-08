package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import wtf.bhopper.nonsense.gui.click.imgui.ImClickGui;
import wtf.bhopper.nonsense.gui.click.novoline.NovoGui;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.annotations.DisplayName;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.ColorProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;
import wtf.bhopper.nonsense.util.render.ColorUtil;

@ModuleInfo(name = "Click GUI",
        description = "Configure the Click GUI.",
        category = ModuleCategory.VISUAL,
        bind = Keyboard.KEY_RSHIFT)
public class ClickGui extends Module {

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Which Click GUI to use.", Mode.NOVOLINE);
    public final BooleanProperty categoryColors = new BooleanProperty("Category Colors", "Category colors.", false);
    public final BooleanProperty toolTips = new BooleanProperty("Tool Tips", "Renders tool tips.", false);
    public final EnumProperty<Sound> sound = new EnumProperty<>("Sound", "Plays a sound when you press a button", Sound.NONE);

    private NovoGui novoline;
    private ImClickGui imGui;

    public ClickGui() {
        this.addProperties(this.mode, this.categoryColors, this.toolTips, this.sound);
    }

    public void initGuis() {
        this.novoline = new NovoGui();
        this.imGui = new ImClickGui();
    }

    @Override
    public void onEnable() {
        this.toggle(false);
        if (mc.currentScreen == null) {

            switch (this.mode.get()) {
                case NOVOLINE -> mc.displayGuiScreen(this.novoline);
                case IMGUI -> mc.displayGuiScreen(this.imGui);
                default -> ChatUtil.error("Someone has been tampering with the Click GUI's!");
            }
        }
    }

    private enum Mode {
        NOVOLINE,
        @DisplayName("ImGUI") IMGUI
    }
    
    public enum Sound {
        CLICK(new ResourceLocation("gui.button.press")),
        NONE(null);

        private final ResourceLocation sound;
        private final float pitch;

        Sound(ResourceLocation location) {
            this.sound = location;
            this.pitch = 1.0F;
        }

        public void playSound(SoundHandler handler) {
            if (this.sound != null) {
                handler.playSound(PositionedSoundRecord.create(this.sound, this.pitch));
            }
        }

    }

}
