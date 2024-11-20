package wtf.bhopper.nonsense.gui.click.novoline;

import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.property.Property;
import wtf.bhopper.nonsense.module.property.impl.*;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.*;

public class NovoModule extends NovoComponent {

    private final Module module;

    private boolean expanded = false;
    private final List<NovoComponent> components = new ArrayList<>();

    public NovoModule(NovoPanel panel, Module module) {
        super(panel, 0);
        this.module = module;

        for (Property<?> p : module.getProperties()) {
            if (p instanceof BooleanProperty booleanProperty) components.add(new NovoSwitch(this.panel, booleanProperty, 0));
            else if (p instanceof NumberProperty numberProperty) components.add(new NovoSlider(this.panel, numberProperty, 0));
            else if (p instanceof EnumProperty<?> enumProperty) components.add(new NovoSelector(this.panel, enumProperty, 0));
            else if (p instanceof ColorProperty colorProperty) components.add(new NovoColorPicker(this.panel, colorProperty, 0));
            else if (p instanceof GroupProperty groupProperty) components.add(new NovoGroup(this.panel, groupProperty, 0));
        }
    }

    @Override
    public void draw(float mouseX, float mouseY, float delta) {

        this.yOff = this.panel.yOff;

        boolean intersecting = this.mouseIntersecting(mouseX, mouseY, MOD_HEIGHT);

        int color;
        if (this.module.isToggled()) {
            color = NovoGui.getColor(this.panel);
        } else if (intersecting) {
            color = BG_COLOR_DARK;
        } else {
            color = BG_COLOR;
        }

        if (intersecting) {
            this.setToolTip(this.module.description);
        }

        this.drawBackground(MOD_HEIGHT, color);

        // TODO: fix text positioning
        NVGHelper.fontSize(16.0F);
        NVGHelper.textAlign(NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
        NVGHelper.drawText(this.module.displayName, 8.0F, yOff + MOD_HEIGHT / 2.0F + 1.0F, 0xFFFFFFFF);

        if (!this.components.isEmpty()) {
            NVGHelper.drawLine(WIDTH - 20.0F, yOff + MOD_HEIGHT / 2.0F, WIDTH - 8.0F, yOff + MOD_HEIGHT / 2.0F, 1.0F, 0xFFDDDDDD);
            if (!this.expanded) {
                NVGHelper.drawLine(WIDTH - 14.0F, yOff + MOD_HEIGHT / 2.0F - 6.0F, WIDTH - 14.0F, yOff + MOD_HEIGHT / 2.0F + 6.0F, 1.0F, 0xFFDDDDDD);
            }
        }

        this.panel.yOff += MOD_HEIGHT;

        if (!this.components.isEmpty() && this.expanded) {

            this.drawBackground(this.panel.yOff, 4.0F, BG_COLOR);
            this.panel.yOff += 4;

            for (NovoComponent component : this.components) {
                component.draw(mouseX, mouseY, delta);
            }

            this.drawBackground(this.panel.yOff, 4.0F, BG_COLOR);
            this.panel.yOff += 4;
        }
    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int button) {
        if (this.mouseIntersecting(mouseX, mouseY, MOD_HEIGHT)) {
            if (button == 0) {
                this.module.toggle();
            } else if (button == 1) {
                this.expanded = !this.expanded;
            }
        } else if (this.expanded) {
            for (NovoComponent component : this.components) {
                component.mouseClick(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void mouseRelease(float mouseX, float mouseY, int button) {
        for (NovoComponent component : this.components) {
            component.mouseRelease(mouseX, mouseY, button);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.expanded) {
            for (NovoComponent component : this.components) {
                component.keyTyped(typedChar, keyCode);
            }
        }
    }

}
