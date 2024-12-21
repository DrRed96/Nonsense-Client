package wtf.bhopper.nonsense.module.impl.visual;

import org.lwjglx.opengl.Display;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.NVGHelper;

@ModuleInfo(name = "Crosshair",
        description = "Draws a custom crosshair",
        category = ModuleCategory.VISUAL)
public class Crosshair extends Module {

    private final NumberProperty size = new NumberProperty("Size", "Size of the crosshair", 16, 1, 30, 1);

    public Crosshair() {
        this.autoAddProperties();
    }

    public void draw(float delta) {

        if (mc.gameSettings.thirdPersonView != 0) {
            return;
        }

        int x = Display.getWidth() / 2;
        int y = Display.getHeight() / 2;


        NVGHelper.begin();

        NVGHelper.drawRect(x - 2.0F, y - 2.0F, 4.0F, 4.0F, ColorUtil.BLACK);
        NVGHelper.drawRect(x - 1.0F, y - 1.0F, 2.0F, 2.0F, ColorUtil.RED);

        NVGHelper.end();

    }

}
