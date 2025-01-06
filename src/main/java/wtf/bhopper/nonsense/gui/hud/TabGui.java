package wtf.bhopper.nonsense.gui.hud;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.client.EventKeyPress;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.impl.visual.HudMod;
import wtf.bhopper.nonsense.module.property.Property;
import wtf.bhopper.nonsense.util.minecraft.IMinecraft;
import wtf.bhopper.nonsense.util.render.*;

import static org.lwjgl.nanovg.NanoVG.*;

public class TabGui implements IMinecraft {

    private ModuleCategory selectedCategory = ModuleCategory.values()[0];
    private Module selectedModule = null;
    private Property<?> selectedProperty = null;

    private boolean isInCategory = false;
    private boolean isInModule = false;
    private boolean isInProperty = false;

    private final Translate categoryTranslate = new Translate(0.0F, 0.0F);

    public void draw(ScaledResolution scaledRes, float delta, int minY) {

        HudMod mod = Hud.mod();

        if (!Hud.enabled() || !mod.tabGui.get()) {
            return;
        }

        float tabHeight = mod.fontSize() + 4.0F;
        float tabSize = tabHeight * ModuleCategory.values().length;

        categoryTranslate.interpolate(0.0F, selectedCategory.ordinal() * tabHeight, 0.2F, delta);

        Hud.WidthMethod getWidth = mod.font.is(HudMod.Font.MINECRAFT)
                    ? text -> Fonts.mc().getStringWidthF(text) * 2.0F
                    : NVGHelper::getStringWidth;

        GlStateManager.pushMatrix();
        scaledRes.scaleToOne();

        NVGHelper.begin();
        NVGHelper.translate(4.0F, minY + 4.0F);

        NVGHelper.drawRect(0.0F, 0.0F, tabSize, tabSize, ColorUtil.BACKGROUND);

        Hud.rect(0.0F, this.categoryTranslate.getY(), tabSize, tabHeight);

        if (mod.font.is(HudMod.Font.MINECRAFT)) {
            NVGHelper.end();

            int count = 0;
            for (ModuleCategory category : ModuleCategory.values()) {
                RenderUtil.drawScaledString(category.name, 8.0F, minY + 6.0F + count * tabHeight, selectedCategory == category ? ColorUtil.WHITE : ColorUtil.DARK_GRAY, true, 2.0F);
                count++;
            }

            NVGHelper.begin();
            NVGHelper.translate(4.0F, minY + 4.0F);
        } else {
            NVGHelper.fontFace(mod.font.get().font);
            NVGHelper.fontSize(mod.fontSize());
            NVGHelper.textAlign(NVG_ALIGN_LEFT | NVG_ALIGN_TOP);

            int count = 0;
            for (ModuleCategory category : ModuleCategory.values()) {
                NVGHelper.drawText(category.name, 4.0F, 2.0F + count * tabHeight, selectedCategory == category ? ColorUtil.WHITE : ColorUtil.DARK_GRAY, true);
                count++;
            }
        }

        if (this.isInCategory) {

        }

        NVGHelper.end();

        GlStateManager.popMatrix();
    }

    @EventLink
    public final Listener<EventKeyPress> onKeyPress = event -> {

        if (mc.currentScreen != null || !Hud.enabled() || !Hud.mod().tabGui.get()) {
            return;
        }

        if (this.isInProperty) {

        } else if (this.isInModule) {

        } else if (this.isInCategory) {
            switch (event.key) {
                case Keyboard.KEY_LEFT -> this.isInCategory = false;
            }
        } else {
            switch (event.key) {
                case Keyboard.KEY_DOWN -> {
                    int ordinal = this.selectedCategory.ordinal() + 1;
                    if (ordinal >= ModuleCategory.values().length) {
                        ordinal = 0;
                    }
                    this.selectedCategory = ModuleCategory.values()[ordinal];
                }
                case Keyboard.KEY_UP -> {
                    int ordinal = this.selectedCategory.ordinal() - 1;
                    if (ordinal <= -1) {
                        ordinal = ModuleCategory.values().length - 1;
                    }
                    this.selectedCategory = ModuleCategory.values()[ordinal];
                }
                case Keyboard.KEY_RIGHT -> this.isInCategory = true;
            }
        }


    };

}
