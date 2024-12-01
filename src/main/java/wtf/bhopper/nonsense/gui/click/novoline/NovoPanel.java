package wtf.bhopper.nonsense.gui.click.novoline;

import org.lwjglx.input.Keyboard;
import org.lwjglx.opengl.Display;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.util.render.Fonts;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;

public class NovoPanel extends NovoComponent {

    protected final NovoGui gui;
    private final ModuleCategory category;

    private final List<NovoModule> modules = new ArrayList<>();

    public int x;
    public int y;

    public boolean moving;
    public int moveX;
    public int moveY;

    private boolean expanded = false;

    public NovoPanel(NovoGui gui, ModuleCategory category, int x) {
        super(null, 0);
        this.gui = gui;
        this.category = category;
        this.x = x;
        this.y = 10;

        for (Module module : Nonsense.getModuleManager().getInCategory(category)) {
            modules.add(new NovoModule(this, module));
        }
    }

    @Override
    public void draw(float mouseX, float mouseY, float delta) {

        NVGHelper.translate(x, y);

        NVGHelper.drawRect(0.0F, 0.0F, WIDTH, HEADER_HEIGHT, NovoComponent.OUTLINE_COLOR);
        NVGHelper.drawRect(1.0F, 1.0F, WIDTH - 2.0F, HEADER_HEIGHT - 1.0F, NovoComponent.BG_COLOR_DARK);

        NVGHelper.fontSize(20.0F);
        NVGHelper.fontFace(Fonts.SEGOE);
        NVGHelper.textAlign(NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
        NVGHelper.drawText(this.category.name, 8.0F, HEADER_HEIGHT / 2.0F, 0xFFFFFFFF, true);

        this.yOff = HEADER_HEIGHT;
        NVGHelper.fontFace(Fonts.SEGOE);

        if (this.expanded) {
            NVGHelper.scissor(0.0F, 0.0F, WIDTH, Display.getHeight());
            for (NovoModule module : this.modules) {
                module.draw(mouseX, mouseY, delta);
            }
            NVGHelper.resetScissor();
        }

        NVGHelper.drawRect(0.0F, Math.min(this.yOff, HEADER_HEIGHT + NovoComponent.HEIGHT), WIDTH, 1.0F, NovoComponent.OUTLINE_COLOR);

        NVGHelper.resetTransform();
    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int button) {
        if (this.mouseIntersecting(mouseX, mouseY, 0.0F, HEADER_HEIGHT)) {
            if (button == 0) {
                this.moving = true;
                this.moveX = (int)mouseX;
                this.moveY = (int)mouseY;
            } else if (button == 1) {
                this.expanded = !this.expanded;
            }
        } else if (this.expanded) {
            for (NovoModule module : this.modules) {
                module.mouseClick(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void mouseRelease(float mouseX, float mouseY, int button) {
        this.moving = false;
        if (this.expanded) {
            for (NovoModule module : this.modules) {
                module.mouseRelease(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

        // TODO: temporary solution until I can find a way to get the scroll wheel to work
        if (keyCode == Keyboard.KEY_UP) {
            this.y += 20.0F;
        } else if (keyCode == Keyboard.KEY_DOWN) {
            this.y -= 20.0F;
        }

        if (this.expanded) {
            for (NovoModule module : this.modules) {
                module.keyTyped(typedChar, keyCode);
            }
        }
    }

    public ModuleCategory getCategory() {
        return this.category;
    }

}
