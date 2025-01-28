package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import wtf.bhopper.nonsense.gui.click.compact.CompactGui;
import wtf.bhopper.nonsense.gui.click.novoline.NovoGui;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;

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
    private CompactGui compact;

    public ClickGui() {
        super();
        this.addProperties(this.mode, this.categoryColors, this.toolTips, this.sound);
    }

    public void initGuis() {
        this.novoline = new NovoGui();
        this.compact = new CompactGui();
    }

    @Override
    public void onEnable() {
        this.toggle(false);

        // Only display the ClickGUI if another screen isn't already open
        if (mc.currentScreen == null) {
            switch (this.mode.get()) {
                case NOVOLINE -> mc.displayGuiScreen(this.novoline);
                case COMPACT -> mc.displayGuiScreen(this.compact);
                default -> ChatUtil.error("Someone has been tampering with the Click GUI's!");
            }
        }
    }

    private enum Mode {
        NOVOLINE,
        COMPACT
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
