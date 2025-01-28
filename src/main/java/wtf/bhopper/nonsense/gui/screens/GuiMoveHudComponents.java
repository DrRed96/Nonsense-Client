package wtf.bhopper.nonsense.gui.screens;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.Display;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.components.RenderComponent;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.util.misc.InputUtil;
import wtf.bhopper.nonsense.util.misc.Vec2i;
import wtf.bhopper.nonsense.util.render.Fonts;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import java.io.IOException;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_TOP;

public class GuiMoveHudComponents extends GuiScreen {

    private static final String NAME = "Component Editor";

    private RenderComponent dragging = null;
    private int dragX = 0;
    private int dragY = 0;

    @Override
    public void drawScreen(int ignoredX, int ignoredY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);

        Vec2i mousePos = InputUtil.getUnscaledMousePositions();

        if (this.dragging != null) {
            this.dragging.setX(mousePos.x - this.dragX);
            this.dragging.setY(mousePos.y - this.dragY);
        }

        float textX = Display.getWidth() / 2.0F;
        float textY = 12.0F;
        float[] bounds = new float[4];

        NVGHelper.begin();
        NVGHelper.fontFace(Fonts.SEGOE_BOLD);
        NVGHelper.fontSize(36.0F);
        NVGHelper.textAlign(NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
        NVGHelper.textBounds(textX, textY, NAME, bounds);
        NVGHelper.drawRoundedRect(bounds[0] - 6.0F, bounds[1] - 4.0F, bounds[2] - bounds[0] + 12.0F, bounds[3] - bounds[1] + 8.0F, 8.0F, 0x80000000);
        NVGHelper.drawText(NAME, textX, textY, Hud.color(), true);
        NVGHelper.end();

        Nonsense.getHud().drawComponents(sr, partialTicks, true, true);
    }

    @Override
    protected void mouseClicked(int ignoredX, int ignoredY, int mouseButton) throws IOException {
        if (mouseButton != 0) {
            return;
        }

        Vec2i mousePos = InputUtil.getUnscaledMousePositions();
        for (RenderComponent component : Nonsense.getHud().getComponents()) {
            if (component.mouseIntersecting(mousePos.x, mousePos.y)) {
                this.dragging = component;
                this.dragX = mousePos.x - component.getX();
                this.dragY = mousePos.y - component.getY();
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.dragging = null;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
