package wtf.bhopper.nonsense.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.impl.visual.HudMod;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_TOP;

public class Watermark {

    private static final NumberFormat TPS_FORMAT = new DecimalFormat("#0.00");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("h:mm a");

    public void draw(ScaledResolution scaledRes) {

        HudMod mod = Hud.mod();

        if (!Hud.enabled() || !mod.watermarkEnabled.get()) {
            return;
        }

        switch (mod.watermarkMode.get()) {
            case EXHIBITION -> {
                String text = this.getText(mod);
                int color = switch (mod.watermarkColorMode.get()) {
                    case WHITE -> ColorUtil.WHITE;
                    case STATIC, SOLID -> mod.color.getRGB();
                    case BREATHING -> ColorUtil.wave(mod.color.getRGB(), System.currentTimeMillis(), 0);
                    case RAINBOW -> ColorUtil.rainbow(0.5F, 1.0F);
                    case RAINBOW_2 -> ColorUtil.rainbow(1.0F, 1.0F);
                };

                if (mod.font.is(HudMod.Font.MINECRAFT)) {
                    if (!mod.watermarkColorMode.is(HudMod.WatermarkColorMode.SOLID)) {
                        text = text.charAt(0) + "\247f" + text.substring(1);
                    }

                    GlStateManager.pushMatrix();
                    scaledRes.scaleToFactor(2.0F);
                    Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(text, 2, 2, color);
                    GlStateManager.popMatrix();

                } else {

                    NVGHelper.begin();

                    NVGHelper.fontFace(mod.font.get().font);
                    NVGHelper.fontSize(mod.fontSize.getFloat());
                    NVGHelper.textAlign(NVG_ALIGN_LEFT | NVG_ALIGN_TOP);

                    String part1 = String.valueOf(text.charAt(0));
                    String part2 = text.substring(1);
                    float valueX = 4.0F + NVGHelper.getStringWidth(part1);

                    NVGHelper.drawText(part1, 4.0F, 4.0F, color, true);
                    NVGHelper.drawText(part2, valueX, 4.0F, mod.watermarkColorMode.is(HudMod.WatermarkColorMode.SOLID) ? color : ColorUtil.WHITE, true);

                    NVGHelper.end();
                }
            }

        }

    }

    public String getText(HudMod mod) {

        if (mod.watermarkText.isEmpty()) {
            return Nonsense.NAME;
        }

        return mod.watermarkText.get()
                .replace("%fps%", Integer.toString(Minecraft.getDebugFPS()))
                .replace("%tps%", TPS_FORMAT.format(Nonsense.getTickRate().getTickRate()))
                .replace("%time%", DATE_FORMAT.format(new Date()));
    }

}