package wtf.bhopper.nonsense.gui.click.novoline;

import net.minecraft.client.Minecraft;
import org.lwjglx.opengl.Display;
import org.lwjglx.util.vector.Vector2f;
import wtf.bhopper.nonsense.module.impl.visual.ClickGui;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.ButtonProperty;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import static org.lwjgl.nanovg.NanoVG.*;

public class NovoButton extends NovoComponent {

    private final ButtonProperty property;

    public NovoButton(NovoPanel panel, ButtonProperty property, int indention) {
        super(panel, indention);
        this.property = property;
    }

    @Override
    public void draw(float mouseX, float mouseY, float delta) {

        if (!this.property.isAvailable()) {
            return;
        }

        if (this.mouseIntersecting(mouseX, mouseY, MOD_HEIGHT)) {
            this.setToolTip(this.property.description);
        }

        this.yOff = this.panel.yOff;

        this.drawBackground(MOD_HEIGHT, this.indentColor(BG_COLOR));

        NVGHelper.fontSize(16.0F);
        NVGHelper.textAlign(NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
        NVGHelper.drawText(this.property.displayName, WIDTH / 2.0F, yOff + MOD_HEIGHT / 2.0F + 1.0F, NovoGui.getColor(this.panel));

        this.panel.yOff += MOD_HEIGHT;
    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int button) {
        if (!this.property.isAvailable()) {
            return;
        }
        if (this.mouseIntersecting(mouseX, mouseY, MOD_HEIGHT) && button == 0) {
            this.panel.gui.mod.sound.get().playSound(Minecraft.getMinecraft().getSoundHandler());
            this.property.execute();
        }
    }

    @Override
    public void mouseRelease(float mouseX, float mouseY, int button) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }
}
