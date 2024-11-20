package wtf.bhopper.nonsense.gui.click.novoline;

import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import static org.lwjgl.nanovg.NanoVG.*;

public class NovoSelector extends NovoComponent {

    private final EnumProperty<?> property;

    public NovoSelector(NovoPanel panel, EnumProperty<?> property, int indention) {
        super(panel, indention);
        this.property = property;
    }

    @Override
    public void draw(float mouseX, float mouseY, float delta) {
        if (!this.property.isAvailable()) {
            return;
        }

        if (this.mouseIntersecting(mouseX, mouseY, MOD_HEIGHT)) {
            this.setToolTip(this.property.getFullDescription());
        }

        this.yOff = this.panel.yOff;

        this.drawBackground(MOD_HEIGHT, this.indentColor(BG_COLOR));

        NVGHelper.fontSize(16.0F);
        NVGHelper.textAlign(NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
        NVGHelper.drawText(this.property.displayName, this.getIndentionOffset(), yOff + MOD_HEIGHT / 2.0F + 1.0F, 0xFFFFFFFF);

        NVGHelper.fontSize(14.0F);
        NVGHelper.textAlign(NVG_ALIGN_RIGHT | NVG_ALIGN_MIDDLE);
        NVGHelper.drawText(this.property.getDisplayValue(), WIDTH - this.getIndentionOffset(), yOff + MOD_HEIGHT / 2.0F, 0xFFFFFFFF);

        this.panel.yOff += MOD_HEIGHT;
    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int button) {

        if (!this.property.isAvailable()) {
            return;
        }

        if (this.mouseIntersecting(mouseX, mouseY, MOD_HEIGHT)) {
            if (button == 0) {
                this.property.cycleForwards();
            } else if (button == 1) {
                this.property.cycleBackwards();
            }
        }
    }

    @Override
    public void mouseRelease(float mouseX, float mouseY, int button) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }
}
