package wtf.bhopper.nonsense.gui.hud;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjglx.opengl.Display;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.impl.visual.HudMod;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.Fonts;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_TOP;

public class ModuleList {

    public static final float ANIMATION_FACTOR = 0.1F;

    private final List<Slot> slots = new ArrayList<>();

    public void init() {
        Nonsense.getModuleManager().getModules().forEach(module -> slots.add(new Slot(module)));
    }

    public int draw(float delta, ScaledResolution scaledRes) {

        if (!Hud.enabled() || !Hud.mod().moduleListEnabled.get()) {
            return 0;
        }

        int right = Display.getWidth();
        float yOff = 0.0F;

        NVGHelper.begin();

        if (!Hud.mod().font.is(HudMod.Font.MINECRAFT)) {
            Hud.bindFont();
            NVGHelper.fontSize(Hud.mod().moduleListFontSize.getFloat());
            NVGHelper.textAlign(NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        }

        this.updateSlots(delta);

        for (Slot slot : this.slots) {
            yOff = slot.draw(yOff, right, scaledRes);
        }

        NVGHelper.end();

        return (int)Math.ceil(yOff);
    }

    private void updateSlots(float delta) {
        this.slots.forEach(slot -> slot.updateText(delta));
        switch (Hud.mod().moduleListSorting.get()) {
            case LENGTH -> this.slots.sort(Comparator.comparingDouble(Slot::getWidth).reversed());
            case ALPHABETICAL -> this.slots.sort(Comparator.comparing(slot -> slot.name));
        }
        long timeMS = System.currentTimeMillis();
        int count = 0;
        for (Slot slot : this.slots) {
            if (slot.shouldDisplay) {
                slot.updateColor(timeMS, count);
                count++;
            }
        }
    }

    public static class Slot {
        private final Module module;

        private boolean shouldDisplay;
        private String name;
        private String suffix;
        private float width;
        private float suffixOffset;
        private int color;
        private float animateFactor;

        public Slot(Module module) {
            this.module = module;
            this.updateText(1.0F);
        }

        public float draw(float yOff, float right, ScaledResolution scaledRes) {

            if (!this.shouldDisplay) {
                return yOff;
            }

            HudMod hudMod = Hud.mod();

            float fontSize = hudMod.font.is(HudMod.Font.MINECRAFT) ? 18.0F : hudMod.moduleListFontSize.getFloat();
            float textX = right - (this.width + hudMod.moduleListSpacing.getFloat()) * this.animateFactor;
            float textY = yOff + hudMod.moduleListSpacing.getFloat();
            float textHeight = fontSize + hudMod.moduleListSpacing.getFloat() * 2.0F;

            if (hudMod.moduleListBackground.getInt() != 0) {
                NVGHelper.drawRect(textX - 2.0F, yOff, right - textX + 2.0F, textHeight, ColorUtil.alpha(0, hudMod.moduleListBackground.getInt()));
            }

            if (hudMod.font.is(HudMod.Font.MINECRAFT)) {
                NVGHelper.end();
                GlStateManager.pushMatrix();
                scaledRes.scaleToFactor(2.0F);

                Fonts.mc().drawStringWithShadow(this.name, textX / 2.0F, textY / 2.0F, this.color);
                if (this.suffix != null) {
                    Fonts.mc().drawStringWithShadow(this.suffix, (textX + this.suffixOffset) / 2.0F, textY / 2.0F, hudMod.moduleListSuffixColor.getRGB());
                }

                GlStateManager.popMatrix();
                NVGHelper.begin();
            } else {
                NVGHelper.drawText(this.name, textX, textY, this.color, true);
                if (this.suffix != null) {
                    NVGHelper.drawText(this.suffix, textX + this.suffixOffset, textY, hudMod.moduleListSuffixColor.getRGB(), true);
                }

            }

            return yOff + textHeight * this.animateFactor;
        }

        public void updateText(float delta) {
            if (this.module.isHidden()) {
                this.shouldDisplay = false;
                this.width = 0.0F;
                this.name = "";
                return;
            }

            HudMod hudMod = Hud.mod();

            if (hudMod.moduleListAnimated.get()) {
                if (this.module.isToggled()) {
                    if (this.animateFactor != 1.0F) {
                        this.animateFactor = Math.min(this.animateFactor + ANIMATION_FACTOR * delta, 1.0F);
                    }
                } else {
                    if (this.animateFactor != 0.0F) {
                        this.animateFactor = Math.max(this.animateFactor - ANIMATION_FACTOR * delta, 0.0F);
                    }
                }
                this.shouldDisplay = this.animateFactor > 0.0F;
            } else {
                this.shouldDisplay = this.module.isToggled();
                this.animateFactor = 1.0F;
            }

            if (!this.shouldDisplay) {
                this.width = 0.0F;
                this.name = "";
                return;
            }

            this.name = hudMod.moduleListDisplay.get() ? module.displayName : module.displayName.replace(" ", "");
            this.suffix = hudMod.moduleListSuffix.get() ? module.getSuffix() : null;

            if (hudMod.moduleListLowerCase.get()) {
                this.name = this.name.toLowerCase();
                if (this.suffix != null) {
                    this.suffix = this.suffix.toLowerCase();
                }
            }

            Hud.WidthMethod getWidth = hudMod.font.is(HudMod.Font.MINECRAFT)
                    ? text -> Fonts.mc().getStringWidthF(text) * 2.0F
                    : NVGHelper::getStringWidth;

            if (this.suffix != null) {
                this.width = getWidth.getWidth(this.name + " " + this.suffix);
                this.suffixOffset = this.width - getWidth.getWidth(this.suffix);
            } else {
                this.width = getWidth.getWidth(this.name);
                this.suffixOffset = 0.0F;
            }

        }

        public void updateColor(long timeMS, int count) {
            this.color = switch (Hud.mod().moduleListColorMode.get()) {
                case STATIC -> Hud.mod().moduleListColor.getRGB();
                case WAVY -> ColorUtil.wave(Hud.mod().moduleListColor.getRGB(), timeMS, count);
                case RAINBOW -> ColorUtil.rainbow(timeMS, count, 0.5F, 1.0F);
                case RAINBOW_2 -> ColorUtil.rainbow(timeMS, count, 1.0F, 1.0F);
                case RAINBOW_3 -> ColorUtil.rainbow(timeMS, count, 0.55F, 0.9F);
                case CATEGORY -> this.module.category.color;
                case ASTOLFO -> ColorUtil.astolfo(timeMS, count);
                case RANDOM -> this.module.hashCode() | 0xFF000000;
                case TRANS -> switch (count % 3) {
                    case 0 -> 0xFF5BCEFA;
                    case 1 -> 0xFFF5A9B8;
                    default -> 0xFFFFFFFF;
                };
            };
        }

        public float getWidth() {
            return this.width;
        }

    }

}
