package wtf.bhopper.nonsense.gui.click.novoline;

import org.lwjglx.opengl.Display;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.util.misc.ResourceUtil;
import wtf.bhopper.nonsense.util.render.Fonts;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.*;

public class NovoPanel extends NovoComponent {

    protected final NovoGui gui;
    private final ModuleCategory category;

    private final List<NovoModule> modules = new ArrayList<>();

    public int x;
    public int y;

    public boolean moving;
    public int moveX;
    public int moveY;

    private boolean expanded = true;

    @SuppressWarnings("FieldCanBeLocal") private final ByteBuffer iconData;
    private final int icon;

    public NovoPanel(NovoGui gui, ModuleCategory category, int x) {
        super(null, 0);
        this.gui = gui;
        this.category = category;
        this.x = x;
        this.y = 10;

        try {
            this.iconData = ResourceUtil.loadResource(this.category.icon);
            this.icon = NVGHelper.createImage(0, this.iconData);
        } catch (IOException | URISyntaxException exception) {
            throw new RuntimeException(exception);
        }

        for (Module module : Nonsense.getModuleManager().getInCategory(category)) {
            modules.add(new NovoModule(this, module));
        }
    }

    @Override
    public void draw(float mouseX, float mouseY, float delta) {

        NVGHelper.translate(x, y);

        NVGHelper.drawRect(0.0F, 0.0F, WIDTH, HEADER_HEIGHT, NovoComponent.OUTLINE_COLOR);
        NVGHelper.drawRect(1.0F, 1.0F, WIDTH - 2.0F, HEADER_HEIGHT - 1.0F, NovoComponent.BG_COLOR_DARK);

        NVGHelper.drawImage(WIDTH - 23.0F, HEADER_HEIGHT + 7.0F, 16.0F, 16.0F, 16.0F, 16.0F, this.icon);

        NVGHelper.fontSize(20.0F);
        NVGHelper.fontFace(Fonts.SEGOE);
        NVGHelper.textAlign(NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
        NVGHelper.drawText(this.category.name, 8.0F, HEADER_HEIGHT / 2.0F, 0xFFFFFFFF, true);

        this.yOff = HEADER_HEIGHT;
        NVGHelper.fontFace(Fonts.SEGOE);

        if (this.expanded) {
            NVGHelper.scissor(0.0F, 0.0F, WIDTH, Display.getHeight() - y);
            for (NovoModule module : this.modules) {
                module.draw(mouseX, mouseY, delta);
            }
            NVGHelper.resetScissor();
        }

        NVGHelper.drawRect(0.0F, this.yOff, WIDTH, 1.0F, NovoComponent.OUTLINE_COLOR);

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
