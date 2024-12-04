package wtf.bhopper.nonsense.gui.hud;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import org.lwjglx.opengl.Display;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.components.RenderComponent;
import wtf.bhopper.nonsense.module.impl.combat.KillAura;
import wtf.bhopper.nonsense.module.impl.visual.HudMod;
import wtf.bhopper.nonsense.util.misc.MathUtil;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.Fonts;
import wtf.bhopper.nonsense.util.render.NVGHelper;
import wtf.bhopper.nonsense.util.render.RenderUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TargetHud extends RenderComponent {

    private static final NumberFormat ASTOLFO_FORMAT = new DecimalFormat("#0.# \u2764");
    private static final NumberFormat RAVEN_FORMAT = new DecimalFormat("#0.0");

    public TargetHud() {
        super("Target HUD", Display.getWidth() / 2 + 20, Display.getHeight() / 2 + 10, 0, 0);
    }

    @Override
    public void draw(float delta, int mouseX, int mouseY, boolean bypass) {
        HudMod mod = Hud.mod();

        EntityLivingBase target = Nonsense.module(KillAura.class).getTarget();
        if (target == null) {
            if (!bypass) {
                return;
            }
            target = mc.thePlayer;
        }

        switch (mod.targetHudMode.get()) {
            case ASTOLFO -> this.drawAstolfoHud(target, mod);
            case RAVEN -> this.drawRavenHud(target, mod);
        }

    }

    private void drawAstolfoHud(EntityLivingBase target, HudMod mod) {

        float health = target.getHealth() + target.getAbsorptionAmount();
        float maxHealth = target.getMaxHealth() + target.getAbsorptionAmount();
        int color = this.getColor(target, mod);

        this.setSize(300, 100);

        NVGHelper.begin();
        this.nvgTranslate();
        this.nvgDrawBackground(0x80000000);
        NVGHelper.drawRect(72.0F, 72.0F, 220.0F, 18.0F, ColorUtil.darken(color, 2));
        NVGHelper.drawRect(72.0F, 72.0F,  (health / maxHealth) * 220.0F, 18.0F, color);
        NVGHelper.end();

        RenderUtil.glColor(ColorUtil.WHITE);
        GuiInventory.drawEntityOnScreen(32, 90, 40, -80, 0, target);

        RenderUtil.drawScaledString(target.getName(), 72.0F, 10.0F, ColorUtil.WHITE, true, 2.0F);
        RenderUtil.drawScaledString(ASTOLFO_FORMAT.format(health), 70.0F, 34.0F, color, true, 4.0F);

    }

    private void drawRavenHud(EntityLivingBase target, HudMod mod) {

        String text = target.getDisplayName().getFormattedText() + " ";
        if (target.getHealth() > target.getMaxHealth() * 0.75F) {
            text += "\247a" + RAVEN_FORMAT.format(target.getHealth()) + " ";
        } else if (target.getHealth() > target.getMaxHealth() * 0.5F) {
            text += "\247e" + RAVEN_FORMAT.format(target.getHealth()) + " ";
        } else if (target.getHealth() > target.getMaxHealth() * 0.25F) {
            text += "\2476" + RAVEN_FORMAT.format(target.getHealth()) + " ";
        } else {
            text += "\247c" + RAVEN_FORMAT.format(target.getHealth()) + " ";
        }
        if (target.getHealth() > mc.thePlayer.getHealth()) {
            text += "\247cL";
        } else if (target.getHealth() < mc.thePlayer.getHealth()) {
            text += "\247aW";
        } else {
            text += "\247eN";
        }

        this.setSize(Fonts.mc().getStringWidth(text) * 2 + 40, 80);

        float healthWidth = (this.getWidth() - 50.0F) * (target.getHealth() / target.getMaxHealth());
        int color = this.getColor(target, mod);
        int color2 = ColorUtil.multiplySatBri(color, 0.5F, 2.0F);

        NVGHelper.begin();
        this.nvgTranslate();

        // Background
        NVGHelper.drawRoundedRect(0.0F, 0.0F, this.getWidth(), this.getHeight(), 10.0F, 0x80000000);

        // Health bar
        NVGHelper.drawRoundedRect(20.0F, 50.0F, this.getWidth() - 40.0F, 10.0F, 5.0F, 0x80000000);
        NVGHelper.beginPath();
        NVGHelper.roundedRect(20.0F, 50.0F, 10.0F + healthWidth, 10.0F, 5.0F);
        NVGHelper.fillPaint(NVGHelper.linearGradient(20.0F, this.getHeight() / 2.0F, 30.0F + healthWidth, this.getHeight() / 2.0F, color2, color));
        NVGHelper.fill();
        NVGHelper.closePath();

        // Outline
        NVGHelper.beginPath();
        NVGHelper.roundedRect(0.0F, 0.0F, this.getWidth(), this.getHeight(), 10.0F);
        NVGHelper.strokePaint(NVGHelper.linearGradient(0.0F, this.getHeight() / 2.0F, this.getWidth(), this.getHeight() / 2.0F, color2, color));
        NVGHelper.strokeWidth(2.0F);
        NVGHelper.stroke();
        NVGHelper.closePath();

        NVGHelper.end();

        RenderUtil.drawScaledString(text, 20.0F, 20.0F, ColorUtil.WHITE, true, 2.0F);
    }

    private int getColor(EntityLivingBase target, HudMod mod) {
        return switch (mod.targetHudColorMode.get()) {
            case STATIC -> mod.color.getRGB();
            case HEALTH -> target.getAbsorptionAmount() != 0.0F ? 0xFFFFAA00 : ColorUtil.health(target);
        };
    }
}
