package wtf.bhopper.nonsense.gui.click.novoline;

import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.util.misc.InputUtil;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.NVGHelper;

public abstract class NovoComponent {

    public static final int WIDTH = 200;
    public static final int HEIGHT = 800; // TODO: scrolling and shit
    public static final int HEADER_HEIGHT = 30;
    public static final int MOD_HEIGHT = 24;
    public static final int PICKER_HEIGHT = 140;
    public static final float DEFAULT_INDENTION = 10.0F;
    public static final float PICKER_OFFSET = 15.0F;

    public static final int BG_COLOR = 0xFF292929;
    public static final int BG_COLOR_DARK = 0xFF1D1D1D;
    public static final int OUTLINE_COLOR = 0xFF070707;
    public static final int COMPONENT_COLOR = 0xFF202020;

    protected final NovoPanel panel;
    protected final int indention;
    protected int yOff;

    public NovoComponent(NovoPanel panel, int indention) {
        this.panel = panel;
        this.indention = indention;
    }

    public abstract void draw(float mouseX, float mouseY, float delta);
    public abstract void mouseClick(float mouseX, float mouseY, int button);
    public abstract void mouseRelease(float mouseX, float mouseY, int button);
    public abstract void keyTyped(char typedChar, int keyCode);

    public void onHidden() {}

    public void drawBackground(float yOff, float height, int color) {
        NVGHelper.drawRect(0.0F, yOff, WIDTH, height, OUTLINE_COLOR);
        NVGHelper.drawRect(1.0F, yOff, WIDTH - 2.0F, height, color);
    }

    public void drawBackground(float height, int color) {
        this.drawBackground(this.yOff, height, color);
    }

    public void drawBackgroundHud(float yOff, float height) {
        NVGHelper.drawRect(0.0F, yOff, WIDTH, height, OUTLINE_COLOR);
        if (NovoGui.mod().categoryColors.get()) {
            NVGHelper.drawRect(1.0F, yOff, WIDTH - 2.0F, height, NovoGui.getColor(this.panel));
        } else {
            Hud.rect(1.0F, yOff, WIDTH - 2.0F, height);
        }
    }

    public void drawBackgroundHud(float height) {
        this.drawBackgroundHud(this.yOff, height);
    }

    public boolean mouseIntersecting(float mouseX, float mouseY, float height) {
        return this.mouseIntersecting(mouseX, mouseY, this.yOff, height);
    }

    public boolean mouseIntersecting(float mouseX, float mouseY, float y, float height) {
        return mouseIntersecting(mouseX, mouseY, 0.0F, y, WIDTH, height);
    }

    public boolean mouseIntersecting(float mouseX, float mouseY, float x, float y, float width, float height) {
        return InputUtil.mouseIntersecting(mouseX, mouseY, x, y, width, height);
    }

    public void setToolTip(String toolTip) {
        this.panel.gui.toolTip = toolTip;
    }

    public float getIndentionOffset() {
        return DEFAULT_INDENTION + this.indention * 2.0F;
    }

    public int indentColor(int color) {
        return ColorUtil.darken(color, this.indention);
    }

    public int indentColor1(int color) {
        return ColorUtil.darken(color, this.indention + 1);
    }

}
