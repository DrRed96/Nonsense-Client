package wtf.bhopper.nonsense.gui.click.novoline;

import net.minecraft.client.gui.GuiScreen;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;
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

    public final ClickGui mod;
    private final List<NovoPanel> panels = new ArrayList<>();

    protected String toolTip = null;

    public NovoGui() {
        this.mod = Nonsense.module(ClickGui.class);
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

            float y = mousePos[1];

            NVGHelper.fontSize(14.0F);
            NVGHelper.textAlign(NVG_ALIGN_LEFT | NVG_ALIGN_BOTTOM);

            float[] bounds = null;

            String[] parts = this.toolTip.split("\n");
            for (int i = parts.length - 1; i >= 0; i--) {
                String tip = parts[i];
                float[] b = new float[4];
                NVGHelper.textBounds(mousePos[0], y, tip, b);
                if (bounds == null) {
                    bounds = b;
                } else {
                    bounds[0] = Math.min(bounds[0], b[0]);
                    bounds[1] = Math.min(bounds[1], b[1]);
                    bounds[2] = Math.max(bounds[2], b[2]);
                    bounds[3] = Math.max(bounds[3], b[3]);
                }
                y -= 16.0F;
            }

            if (bounds != null) {
                NVGHelper.drawRoundedRect(bounds[0] - 2.0F, bounds[1] - 2.0F, bounds[2] - bounds[0] + 4.0F, bounds[3] - bounds[1] + 4.0F, 4.0F, 0x80000000);

                y = mousePos[1];
                for (int i = parts.length - 1; i >= 0; i--) {
                    NVGHelper.drawText(parts[i], mousePos[0], y, 0xFFFFFFFF);
                    y -= 16.0F;
                }

            }
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

        if (isCtrlKeyDown() && keyCode == Keyboard.KEY_R) {
            int count = 0;
            for (NovoPanel panel : this.panels) {
                panel.x = 10 + count * (NovoComponent.WIDTH + 10);
                panel.y = 10;
                count++;
            }
        }

        for (NovoPanel panel : this.panels) {
            panel.keyTyped(typedChar, keyCode);
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

    @Override
    public void handleMouseInput() throws IOException {
        int offset = Mouse.getDWheel() * 20;
        for (NovoPanel panel : this.panels) {
            panel.y += offset;
        }
        super.handleMouseInput();
    }

    public static int getColor(NovoPanel panel) {
        if (mod().categoryColors.get()) {
            return panel.getCategory().color;
        }
        return mod().color.getRGB();
    }

    public static ClickGui mod() {
        return Nonsense.module(ClickGui.class);
    }
}
