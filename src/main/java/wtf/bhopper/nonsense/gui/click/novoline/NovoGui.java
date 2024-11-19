package wtf.bhopper.nonsense.gui.click.novoline;

import net.minecraft.client.gui.GuiScreen;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.impl.visual.ClickGui;
import wtf.bhopper.nonsense.util.misc.InputUtil;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BOTTOM;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;

public class NovoGui extends GuiScreen {

    private final List<NovoPanel> panels = new ArrayList<>();

    protected String toolTip = null;

    public NovoGui() {
        for (ModuleCategory category : ModuleCategory.values()) {
            panels.add(new NovoPanel(this, category, 10 + category.ordinal() * (NovoComponent.WIDTH + 10)));
        }
    }

    @Override
    public void drawScreen(int mouseXIgnored, int mouseYIgnored, float partialTicks) {

        super.drawScreen(mouseXIgnored, mouseYIgnored, partialTicks);

        this.toolTip = null;
        int[] mousePos = InputUtil.getUnscaledMousePositions();

        NVGHelper.begin();
        for (NovoPanel panel : this.panels) {

            if (panel.moving) {
                panel.x = mousePos[0] - panel.moveX;
                panel.y = mousePos[1] - panel.moveY;
            }

            panel.draw(mousePos[0] - panel.x, mousePos[1] - panel.y, partialTicks);
        }

        if (this.toolTip != null && mod().toolTips.get()) {
            float[] bounds = new float[4];
            NVGHelper.fontSize(14.0F);
            NVGHelper.textAlign(NVG_ALIGN_LEFT | NVG_ALIGN_BOTTOM);
            NVGHelper.textBounds(mousePos[0], mousePos[1], this.toolTip, bounds);

            NVGHelper.drawRoundedRect(bounds[0] - 2.0F, bounds[1] - 2.0F, bounds[2] - bounds[0] + 4.0F, bounds[3] - bounds[1] + 4.0F, 2.0F, 0x80000000);
            NVGHelper.drawText(this.toolTip, mousePos[0], mousePos[1], 0xFFFFFFFF);
        }

        NVGHelper.end();
    }

    @Override
    protected void mouseClicked(int mouseXIgnored, int mouseYIgnored, int mouseButton) throws IOException {
        int[] mousePos = InputUtil.getUnscaledMousePositions();
        for (NovoPanel dropdown : this.panels) {
            dropdown.mouseClick(mousePos[0] - dropdown.x, mousePos[1] - dropdown.y, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseXIgnored, int mouseYIgnored, int state) {
        int[] mousePos = InputUtil.getUnscaledMousePositions();
        for (NovoPanel dropdown : this.panels) {
            dropdown.mouseRelease(mousePos[0] - dropdown.x, mousePos[1] - dropdown.y, state);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        for (NovoPanel dropdown : this.panels) {
            dropdown.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public boolean isClientSide() {
        return true;
    }

    public static int getColor(NovoPanel panel) {
        return mod().color.getRGB();
    }

    public static ClickGui mod() {
        return Nonsense.module(ClickGui.class);
    }
}
