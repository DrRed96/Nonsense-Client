package wtf.bhopper.nonsense.gui.click.compact;

import org.lwjgl.nanovg.NanoVG;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.Fonts;
import wtf.bhopper.nonsense.util.render.NVGHelper;

public class CompactModule {

    private final CompactGui owner;
    private final AbstractModule module;

    private int y;

    public CompactModule(CompactGui owner, AbstractModule module, int y) {
        this.owner = owner;
        this.module = module;
        this.y = y;
    }

    public void draw(int mouseX, int mouseY, float delta) {
        NVGHelper.fontFace(Fonts.SEGOE);
        NVGHelper.fontSize(CompactGui.MODULE_FONT_SIZE);
        NVGHelper.textAlign(NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_TOP);
        NVGHelper.drawText(this.module.displayName, 8.0F, this.y + 4.0F, this.module.isToggled() ? ColorUtil.WHITE : ColorUtil.GRAY, true);
    }

    public void onClick(int mouseX, int mouseY, int mouseButton) {
        if (mouseX >= 0 && mouseX < CompactGui.MODULES_SIZE && mouseY >= this.y && mouseY < this.y + 32) {
            switch (mouseButton) {
                case 0 -> this.module.toggle();
                case 1 -> this.owner.expandedModule = this;
            }
        }
    }

    public int getY() {
        return this.y;
    }

}
