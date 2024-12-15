package wtf.bhopper.nonsense.gui.click.novoline;

import net.minecraft.client.Minecraft;
import org.lwjglx.util.vector.Vector2f;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;

public class NovoSwitch extends NovoComponent {

    private final BooleanProperty property;

    public NovoSwitch(NovoPanel panel, BooleanProperty property, int indention) {
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
        NVGHelper.textAlign(NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
        NVGHelper.drawText(this.property.displayName, this.getIndentionOffset(), yOff + MOD_HEIGHT / 2.0F + 1.0F, 0xFFFFFFFF, true);

        float boxX = WIDTH - 24.0F;
        float boxY = yOff + MOD_HEIGHT / 2.0F - 8.0F;
        NVGHelper.drawRect(boxX, boxY, 16.0F, 16.0F, this.indentColor(COMPONENT_COLOR));
        if (this.property.get()) {
            NVGHelper.drawLine(1.5F, 0xFFDDDDDD,
                    new Vector2f(boxX + 1.0F, boxY + 9.0F),
                    new Vector2f(boxX + 6.0F, boxY + 13.0F),
                    new Vector2f(boxX + 15.0F, boxY + 2.0F));
        }

        this.panel.yOff += MOD_HEIGHT;
    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int button) {
        if (!this.property.isAvailable()) {
            return;
        }
        if (this.mouseIntersecting(mouseX, mouseY, MOD_HEIGHT) && button == 0) {
            this.property.toggle();
            this.panel.gui.mod.sound.get().playSound(Minecraft.getMinecraft().getSoundHandler());
        }
    }

    @Override
    public void mouseRelease(float mouseX, float mouseY, int button) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }
}
