package wtf.bhopper.nonsense.gui.click.novoline;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import wtf.bhopper.nonsense.module.property.impl.StringProperty;
import wtf.bhopper.nonsense.util.misc.Stopwatch;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import static org.lwjgl.nanovg.NanoVG.*;

public class NovoTextBox extends NovoComponent {

    private static final int EXPANDED_HEIGHT = 18;

    private final StringProperty property;

    private boolean expanded = false;

    private boolean showType = true;
    private final Stopwatch typeTimer = new Stopwatch();

    public NovoTextBox(NovoPanel panel, StringProperty property, int indention) {
        super(panel, indention);
        this.property = property;
    }

    @Override
    public void draw(float mouseX, float mouseY, float delta) {

        if (!this.property.isAvailable()) {
            this.expanded = false;
            return;
        }

        if (this.mouseIntersecting(mouseX, mouseY, MOD_HEIGHT)) {
            this.setToolTip(this.property.description);
        }

        this.yOff = this.panel.yOff;

        this.drawBackground(MOD_HEIGHT, this.indentColor(BG_COLOR));

        NVGHelper.fontSize(16.0F);
        NVGHelper.textAlign(NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
        NVGHelper.drawText(this.property.displayName + ": ", this.getIndentionOffset(), yOff + MOD_HEIGHT / 2.0F + 1.0F, 0xFFFFFFFF, true);

        if (this.expanded) {
            if (this.typeTimer.hasReached(1000)) {
                this.showType = !this.showType;
                this.typeTimer.reset();
            }

            this.panel.yOff += MOD_HEIGHT;

            NVGHelper.fontSize(14.0F);
            NVGHelper.textAlign(NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);

            String displayValue = this.property.get() + (this.showType ? "|" : "");
            float lineBreakWidth = WIDTH - this.getIndentionOffset() * 2;
            float[] bounds = new float[4];
            NVGHelper.textBoxBounds(this.getIndentionOffset(), this.panel.yOff + EXPANDED_HEIGHT / 2.0F + 1.0F, lineBreakWidth, displayValue, bounds);
            float height = bounds[3] - bounds[1] + 5.0F;

            this.drawBackground(this.panel.yOff, height, this.indentColor(BG_COLOR));

            NVGHelper.drawTextBox(this.property.get() + (this.showType ? "|" : ""), this.getIndentionOffset(), this.panel.yOff + EXPANDED_HEIGHT / 2.0F + 1.0F, 0xFFDDDDDD, true, lineBreakWidth);

            this.panel.yOff += (int)height;
        } else {
            float offset = this.getIndentionOffset() + NVGHelper.getStringWidth(this.property.displayName + ": ");

            NVGHelper.fontSize(14.0F);
            NVGHelper.textAlign(NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
            NVGHelper.drawText(this.property.get(), offset, yOff + MOD_HEIGHT / 2.0F, 0xFFDDDDDD, true);

            this.panel.yOff += MOD_HEIGHT;
        }
    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int button) {
        if (!this.property.isAvailable()) {
            return;
        }

        this.expanded = this.mouseIntersecting(mouseX, mouseY, MOD_HEIGHT + (this.expanded ? 0 : EXPANDED_HEIGHT));

        if (this.expanded) {
            this.showType = true;
            this.typeTimer.reset();
            this.panel.gui.mod.sound.get().playSound(Minecraft.getMinecraft().getSoundHandler());
        }
    }

    @Override
    public void mouseRelease(float mouseX, float mouseY, int button) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

        if (this.expanded) {
            if (keyCode == Keyboard.KEY_BACK) {
                this.property.backspace();
            } else if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                this.property.append(typedChar);
            }
        }
    }

    @Override
    public void onHidden() {
        this.expanded = false;
    }
}
