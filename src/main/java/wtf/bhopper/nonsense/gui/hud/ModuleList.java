package wtf.bhopper.nonsense.gui.hud;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.Display;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.impl.visual.HudMod;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.Fonts;
import wtf.bhopper.nonsense.util.render.NVGHelper;
import wtf.bhopper.nonsense.util.render.RenderUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.*;

public class ModuleList {

    public static final float ANIMATION_FACTOR = 0.1F;

    private final List<Slot> slots = new ArrayList<>();

    public void init() {
        Nonsense.getModuleManager().getModules().forEach(module -> slots.add(new Slot(Hud.mod(), module)));
    }

    public int draw(float delta, ScaledResolution scaledRes) {
        HudMod mod = Hud.mod();

        if (!Hud.enabled() || !mod.moduleListEnabled.get()) {
            return 0;
        }

        int right = Display.getWidth() - (mod.moduleListOutline.is(HudMod.ModuleListOutline.RIGHT) ? 2 : 0);
        float yOff = switch (mod.moduleListMode.get()) {
            case GENERIC -> 0.0F;
        };

        NVGHelper.begin();

        switch (mod.moduleListMode.get()) {
            case GENERIC -> {
                if (!mod.font.is(HudMod.Font.MINECRAFT)) {
                    Hud.bindFont();
                    NVGHelper.fontSize(mod.fontSize.getFloat());
                    NVGHelper.textAlign(NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
                }
            }
        }

        this.updateSlots(mod, delta);

        // Background
        if (mod.moduleListBackground.getInt() != 0) {

            float yOffBg = yOff;

            for (Slot slot : this.slots) {
                yOffBg = slot.drawBackground(yOffBg, right, mod);
            }
        }

        switch (mod.moduleListOutline.get()) {
            case LEFT, RIGHT -> {
                float yOffOutline = yOff;

                for (Slot slot : this.slots) {
                    yOffOutline = slot.drawOutlinePoints(yOffOutline, right, mod);
                }
            }

            case FULL -> {

                float yOffOutline = yOff;

                for (int i = 0; i < this.slots.size(); i++) {
                    if (i == this.slots.size() - 1) {
                        yOffOutline = this.slots.get(i).drawOutlinePointsFull(yOffOutline, right, mod, null);
                        continue;
                    }

                    yOffOutline = this.slots.get(i).drawOutlinePointsFull(yOffOutline, right, mod, this.slots.get(i + 1));
                }

            }
        }

        for (Slot slot : this.slots) {
            yOff = slot.drawText(yOff, right, scaledRes, mod);
        }

        NVGHelper.end();

        return (int)Math.ceil(yOff);
    }

    private void updateSlots(HudMod mod, float delta) {
        this.slots.forEach(slot -> slot.updateText(mod, delta));
        switch (mod.moduleListSorting.get()) {
            case LENGTH -> this.slots.sort(Comparator.comparingDouble(Slot::getWidth).reversed());
            case ALPHABETICAL -> this.slots.sort(Comparator.comparing(slot -> slot.name));
        }
        long timeMS = System.currentTimeMillis();
        int count = 0;
        for (Slot slot : this.slots) {
            if (slot.shouldDisplay) {
                slot.updateColor(mod, timeMS, count);
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

        public Slot(HudMod hudMod, Module module) {
            this.module = module;
            this.updateText(hudMod, 1.0F);
        }

        public float drawText(float yOff, float right, ScaledResolution scaledRes, HudMod hudMod) {

            if (!this.shouldDisplay) {
                return yOff;
            }

            return switch (hudMod.moduleListMode.get()) {
                case GENERIC -> {
                    float fontSize = hudMod.font.is(HudMod.Font.MINECRAFT) ? 18.0F : hudMod.fontSize.getFloat();
                    float textX = right - (this.width + 2.0F + hudMod.moduleListSpacing.getFloat()) * this.animateFactor;
                    float textY = yOff + hudMod.moduleListSpacing.getFloat() + 1.0F;
                    float textHeight = fontSize + hudMod.moduleListSpacing.getFloat() * 2.0F;

                    if (hudMod.font.is(HudMod.Font.MINECRAFT)) {
                        NVGHelper.end();
                        GlStateManager.pushMatrix();
                        scaledRes.scaleToFactor(1.0F);

                        RenderUtil.drawScaledString(this.name, textX, textY, this.color, true, 2.0F);
                        if (this.suffix != null) {
                            RenderUtil.drawScaledString(this.suffix, textX + this.suffixOffset, textY, hudMod.moduleListSuffixColor.getRGB(), true, 2.0F);
                        }

                        GlStateManager.popMatrix();
                        NVGHelper.begin();
                    } else {
                        NVGHelper.drawText(this.name, textX, textY, this.color, true);
                        if (this.suffix != null) {
                            NVGHelper.drawText(this.suffix, textX + this.suffixOffset, textY, hudMod.moduleListSuffixColor.getRGB(), true);
                        }
                    }

                    yield yOff + textHeight * this.animateFactor;
                }
            };
        }

        public float drawBackground(float yOff, float right, HudMod hudMod) {

            if (!this.shouldDisplay) {
                return yOff;
            }

            float fontSize = hudMod.font.is(HudMod.Font.MINECRAFT) ? 18.0F : hudMod.fontSize.getFloat();
            float width = (this.width + 2.0F + hudMod.moduleListSpacing.getFloat()) * this.animateFactor;
            float textX = right - width;
            float textHeight = (fontSize + hudMod.moduleListSpacing.getFloat() * 2.0F) * this.animateFactor;

            NVGHelper.drawRect(textX - 2.0F, yOff, width + 2.0F, textHeight, ColorUtil.alpha(0, hudMod.moduleListBackground.getInt()));

            return yOff + textHeight;
        }

        public float drawOutlinePoints(float yOff, float right, HudMod hudMod) {
            if (!this.shouldDisplay) {
                return yOff;
            }

            float fontSize = hudMod.font.is(HudMod.Font.MINECRAFT) ? 18.0F : hudMod.fontSize.getFloat();
            float textX = right - (this.width + 2.0F + hudMod.moduleListSpacing.getFloat()) * this.animateFactor;
            float textHeight = (fontSize + hudMod.moduleListSpacing.getFloat() * 2.0F) * this.animateFactor;

            NVGHelper.beginPath();

            switch (hudMod.moduleListOutline.get()) {
                case LEFT -> {
                    NVGHelper.moveTo(textX - 3.0F, yOff);
                    NVGHelper.lineTo(textX - 3.0F, yOff + textHeight * this.animateFactor);
                }
                case RIGHT -> {
                    NVGHelper.moveTo(right + 1.0F, yOff);
                    NVGHelper.lineTo(right + 1.0F, yOff + textHeight * this.animateFactor);
                }
            }

            NVGHelper.strokeColor(this.color);
            NVGHelper.strokeWidth(2.0F);
            NVGHelper.stroke();
            NVGHelper.closePath();

            return yOff + textHeight;
        }

        public float drawOutlinePointsFull(float yOff, float right, HudMod hudMod, Slot nextSlot) {
            if (!this.shouldDisplay) {
                return yOff;
            }

            float fontSize = hudMod.font.is(HudMod.Font.MINECRAFT) ? 18.0F : hudMod.fontSize.getFloat();
            float textX = right - (this.width + 2.0F + hudMod.moduleListSpacing.getFloat()) * this.animateFactor;
            float nextTextX = nextSlot == null ? Display.getWidth() + 5.0F : right - (nextSlot.width + 2.0F + hudMod.moduleListSpacing.getFloat()) * nextSlot.animateFactor;
            float textHeight = (fontSize + hudMod.moduleListSpacing.getFloat() * 2.0F) * this.animateFactor;

            NVGHelper.beginPath();

            NVGHelper.moveTo(textX - 3.0F, yOff);
            NVGHelper.lineTo(textX - 3.0F, yOff + textHeight * this.animateFactor);
            NVGHelper.lineTo(nextTextX - 2.0F, yOff + textHeight * this.animateFactor);

            NVGHelper.strokeColor(this.color);
            NVGHelper.strokeWidth(2.0F);
            NVGHelper.stroke();
            NVGHelper.closePath();

            return yOff + textHeight;
        }

        public void updateText(HudMod hudMod, float delta) {
            if (this.module.isHidden()) {
                this.shouldDisplay = false;
                this.width = 0.0F;
                this.name = "";
                return;
            }

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

            String rawSuffix = module.getSuffix();
            this.name = hudMod.moduleListDisplay.get() ? module.displayName : module.displayName.replace(" ", "");
            assert name != null; // Just to get rid of an annoying error
            if (rawSuffix != null) {
                this.suffix = switch (hudMod.moduleListMode.get()) {
                    case GENERIC -> switch (hudMod.moduleListSuffix.get()) {
                        case NORMAL -> module.getSuffix();
                        case HYPHEN -> "- " + module.getSuffix();
                        case BRACKET -> "(" + module.getSuffix() + ")";
                        case SQUARE -> "[" + module.getSuffix() + "]";
                        case ANGLE -> "<" + module.getSuffix() + ">";
                        case CURLY -> "{" + module.getSuffix() + "}";
                        case NONE -> null;
                    };
                };
            } else {
                this.suffix = null;
            }

            if (hudMod.moduleListLowerCase.get()) {
                this.name = this.name.toLowerCase();
                if (this.suffix != null) {
                    this.suffix = this.suffix.toLowerCase();
                }
            }

            Hud.WidthMethod getWidth = switch (hudMod.moduleListMode.get()) {
                case GENERIC -> hudMod.font.is(HudMod.Font.MINECRAFT)
                        ? text -> Fonts.mc().getStringWidthF(text) * 2.0F
                        : NVGHelper::getStringWidth;
            };

            if (this.suffix != null) {
                this.width = getWidth.getWidth(this.name + " " + this.suffix);
                this.suffixOffset = getWidth.getWidth(this.name + " ");
            } else {
                this.width = getWidth.getWidth(this.name);
                this.suffixOffset = 0.0F;
            }

        }

        public void updateColor(HudMod mod, long timeMS, int count) {

            this.color = switch (mod.moduleListMode.get()) {
                case GENERIC -> switch (mod.moduleListColor.get()) {
                    case STATIC -> mod.color.getRGB();
                    case WAVY -> Hud.colorWave(timeMS, count);
                    case RAINBOW -> ColorUtil.rainbow(timeMS, count, 0.5F, 1.0F);
                    case RAINBOW_2 -> ColorUtil.rainbow(timeMS, count, 1.0F, 1.0F);
                    case EXHIBITION_RAINBOW -> ColorUtil.exhiRainbow(timeMS, count);
                    case CATEGORY -> this.module.category.color;
                    case ASTOLFO -> ColorUtil.astolfo(timeMS, count);
                    case RANDOM -> this.module.hashCode() | 0xFF000000;
                    case TRANS -> switch (count % 3) {
                        case 0 -> 0xFF5BCEFA; // I legit Googled the exact RGB values XD
                        case 1 -> 0xFFF5A9B8;
                        default -> 0xFFFFFFFF;
                    };
                };
            };
        }

        public float getWidth() {
            return this.width;
        }

    }

}
