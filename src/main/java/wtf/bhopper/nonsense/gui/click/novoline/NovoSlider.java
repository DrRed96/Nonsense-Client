package wtf.bhopper.nonsense.gui.click.novoline;

import net.minecraft.client.Minecraft;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import static org.lwjgl.nanovg.NanoVG.*;

public class NovoSlider extends NovoComponent {

    private final NumberProperty property;
    private boolean selected = false;

    public NovoSlider(NovoPanel panel, NumberProperty property, int indention) {
        super(panel, indention);
        this.property = property;
    }

    @Override
    public void draw(float mouseX, float mouseY, float delta) {

        if (!this.property.isAvailable()) {
            this.selected = false;
            return;
        }

        if (this.mouseIntersecting(mouseX, mouseY, MOD_HEIGHT)) {
            this.setToolTip(this.property.description);
        }

        this.yOff = this.panel.yOff;

        this.drawBackground(MOD_HEIGHT, this.indentColor(BG_COLOR));

        float boxY = yOff + MOD_HEIGHT / 2.0F - 10.0F;
        float width = WIDTH - 16.0F;
        NVGHelper.drawRect(8.0F, boxY, width, 20.0F, this.indentColor(COMPONENT_COLOR));
        NVGHelper.drawRect(8.0F, boxY, width * (float)this.property.getPercent(), 20.0F, NovoGui.getColor(this.panel));

        NVGHelper.fontSize(16.0F);
        NVGHelper.textAlign(NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
        NVGHelper.drawText(this.property.displayName, this.getIndentionOffset(), yOff + MOD_HEIGHT / 2.0F + 1.0F, 0xFFFFFFFF, true);

        NVGHelper.textAlign(NVG_ALIGN_RIGHT | NVG_ALIGN_MIDDLE);
        NVGHelper.drawText(this.property.getDisplayValue(), WIDTH - this.getIndentionOffset(), yOff + MOD_HEIGHT / 2.0F, 0xFFDDDDDD, true);

        // this fixes a weird rendering error, don't ask me how...
        NVGHelper.beginPath();
        NVGHelper.stroke();
        NVGHelper.closePath();

        if (this.selected) {
            this.property.setFromPercent((mouseX - 16.0F) / width);
        }

        this.panel.yOff += MOD_HEIGHT;
    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int button) {
        if (!this.property.isAvailable()) {
            return;
        }

        if (this.mouseIntersecting(mouseX, mouseY, MOD_HEIGHT) && button == 0) {
            this.selected = true;
            this.panel.gui.mod.sound.get().playSound(Minecraft.getMinecraft().getSoundHandler());
        }
    }

    @Override
    public void mouseRelease(float mouseX, float mouseY, int button) {
        if (!this.property.isAvailable()) {
            return;
        }
        this.selected = false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }
}
