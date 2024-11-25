package wtf.bhopper.nonsense.gui.click.novoline;

import net.minecraft.util.ResourceLocation;
import org.lwjglx.opengl.Display;
import wtf.bhopper.nonsense.module.property.impl.ColorProperty;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import java.awt.*;

import static org.lwjgl.nanovg.NanoVG.*;

public class NovoColorPicker extends NovoComponent {

    private static int transparent = -1;

    private final ColorProperty property;
    private boolean expanded = false;

    private boolean sbSelected = false;
    private boolean hueSelected = false;
    private boolean alphaSelected = false;

    public NovoColorPicker(NovoPanel panel, ColorProperty property, int indention) {
        super(panel, indention);
        this.property = property;

        if (transparent == -1) {
            transparent = NVGHelper.createImage(new ResourceLocation("nonsense/transparent.jpg"), 0);
        }
    }

    @Override
    public void draw(float mouseX, float mouseY, float delta) {

        if (!this.property.isAvailable()) {
            this.expanded = false;
            this.sbSelected = false;
            this.hueSelected = false;
            this.alphaSelected = false;
            return;
        }

        if (this.mouseIntersecting(mouseX, mouseY, MOD_HEIGHT)) {
            this.setToolTip(this.property.description);
        }

        this.yOff = this.panel.yOff;

        this.drawBackground(this.expanded ? MOD_HEIGHT + PICKER_HEIGHT : MOD_HEIGHT, this.indentColor(BG_COLOR));

        NVGHelper.fontSize(16.0F);
        NVGHelper.textAlign(NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
        NVGHelper.drawText(this.property.displayName, 12.0F, yOff + MOD_HEIGHT / 2.0F + 1.0F, 0xFFFFFFFF);

        float boxX = WIDTH - 24.0F;
        float boxY = yOff + MOD_HEIGHT / 2.0F - 8.0F;
        NVGHelper.textAlign(NVG_ALIGN_RIGHT | NVG_ALIGN_MIDDLE);
        NVGHelper.drawText(this.property.getDisplayValueNoAlpha(), boxX - 3.0F, yOff + MOD_HEIGHT / 2.0F + 1.0F, 0xFFFFFFFF);
        NVGHelper.drawRect(boxX, boxY, 16.0F, 16.0F, OUTLINE_COLOR);
        NVGHelper.drawImage(boxX + 1.0F, boxY + 1.0F, 14.0F, 14.0F, 400.0F, 400.0F, transparent);
        NVGHelper.drawRect(boxX + 1.0F, boxY + 1.0F, 14.0F, 14.0F, property.getRGB());

        this.panel.yOff += MOD_HEIGHT;

        if (this.expanded) {

            float[] hsb = this.property.getHSB();
            float alpha = this.property.getAlpha() / 255.0F;

            float pos = this.yOff + MOD_HEIGHT + 8.0F;

            // Saturation / Brightness
            NVGHelper.drawRect(PICKER_OFFSET - 1.0F, pos - 1.0F, 122.0F, 122.0F, OUTLINE_COLOR);
            NVGHelper.drawColorPicker(PICKER_OFFSET, pos, 120.0F, 120.0F, hsb[0]);

            NVGHelper.drawCircle(PICKER_OFFSET + hsb[1] * 120.0F, pos + (1.0F - hsb[2]) * 120.0F, 3.0F, ColorUtil.alpha(this.property.getRGB(), 0xFF));
            NVGHelper.drawOutlineCircle(PICKER_OFFSET + hsb[1] * 120.0F, pos + (1.0F - hsb[2]) * 120.0F, 4.0F, 1.5F, OUTLINE_COLOR);

            // Hue
            NVGHelper.drawRect(PICKER_OFFSET + 124.0F, pos - 1.0F, 22.0F, 122.0F, OUTLINE_COLOR);
            NVGHelper.drawHueBar(PICKER_OFFSET + 125.0F, pos, 20.0F, 120.0F);
            NVGHelper.drawRect(PICKER_OFFSET + 128.0F, pos + hsb[0] * 120.0F, 14.0F, 1.0F, 0xFFDDDDDD);

            // Alpha
            NVGHelper.drawRect(PICKER_OFFSET + 149.0F, pos - 1.0F, 22.0F, 122.0F, OUTLINE_COLOR);
            NVGHelper.drawImage(PICKER_OFFSET + 150.0F, pos, 20.0F, 120.0F, 400.0F, 400.0F, transparent);
            NVGHelper.drawGraidentRect(PICKER_OFFSET + 150.0F, pos, 20.0F, 120.0F,
                    Color.HSBtoRGB(hsb[0], 1.0F, 1.0F),
                    ColorUtil.alpha(Color.HSBtoRGB(hsb[0], 1.0F, 1.0F), 0));
            NVGHelper.drawRect(PICKER_OFFSET + 153.0F, pos + (1.0F - alpha) * 120.0F, 14.0F, 1.0F, OUTLINE_COLOR);

            this.panel.yOff += PICKER_HEIGHT;


            if (this.sbSelected) {
                this.property.setSaturation((mouseX - PICKER_OFFSET) / 120.0F);
                this.property.setBrightness(1.0F - ((mouseY - pos) / 120.0F));
            } else if (this.hueSelected) {
                this.property.setHue((mouseY - pos) / 120.0F);
            } else if (this.alphaSelected) {
                this.property.setAlpha(1.0F - ((mouseY - pos) / 120.0F));
            }

        } else {
            this.sbSelected = false;
            this.hueSelected = false;
            this.alphaSelected = false;
        }
    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int button) {
        if (!this.property.isAvailable()) {
            return;
        }
        if (this.mouseIntersecting(mouseX, mouseY, MOD_HEIGHT) && button == 0) {
            this.expanded = !this.expanded;
        } else {

            if (this.mouseIntersecting(mouseX, mouseY, PICKER_OFFSET, this.yOff + MOD_HEIGHT + 7.0F, 120.0F, 120.0F)) {
                this.sbSelected = true;
            } else if (this.mouseIntersecting(mouseX, mouseY, PICKER_OFFSET + 125.0F, this.yOff + MOD_HEIGHT + 7.0F, 20.0F, 120.0F)) {
                this.hueSelected = true;
            } else if (this.mouseIntersecting(mouseX, mouseY, PICKER_OFFSET + 150.0F, this.yOff + MOD_HEIGHT + 7.0F, 20.0F, 120.0F)) {
                this.alphaSelected = true;
            }

        }
    }

    @Override
    public void mouseRelease(float mouseX, float mouseY, int button) {
        this.sbSelected = false;
        this.hueSelected = false;
        this.alphaSelected = false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }
}
