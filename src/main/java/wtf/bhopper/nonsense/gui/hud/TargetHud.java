package wtf.bhopper.nonsense.gui.hud;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.Display;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.components.RenderComponent;
import wtf.bhopper.nonsense.module.impl.combat.InfiniteAura;
import wtf.bhopper.nonsense.module.impl.combat.KillAura;
import wtf.bhopper.nonsense.module.impl.visual.HudMod;
import wtf.bhopper.nonsense.util.render.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TargetHud extends RenderComponent {

    private static final NumberFormat ASTOLFO_FORMAT = new DecimalFormat("#0.# \u2764");
    private static final NumberFormat RAVEN_FORMAT = new DecimalFormat("#0.0");
    private static final NumberFormat DISTANCE_FORMAT = new DecimalFormat("#0.00'm'");

    private final Translate translate = new Translate(0.0F, 0.0F);

    public TargetHud() {
        super("Target HUD", Display.getWidth() / 2 + 20, Display.getHeight() / 2 + 10, 0, 0);
        Nonsense.module(HudMod.class).targetHudGroup.addProperties(this.getProperties());
        Nonsense.module(HudMod.class).targetHudMode.addValueChangeListener(this::onModeChange);
    }

    @Override
    public void draw(float delta, int mouseX, int mouseY, boolean bypass) {
        HudMod mod = Hud.mod();

        EntityLivingBase target = Nonsense.module(KillAura.class).getTarget();
        if (target == null) {
            target = Nonsense.module(InfiniteAura.class).getTarget();
        }
        if (target == null) {
            if (!bypass) {
                return;
            }
            target = mc.thePlayer;
        }

        switch (mod.targetHudMode.get()) {
            case DETAILED -> this.drawDetailedHud(target, mod, delta);
            case ASTOLFO -> this.drawAstolfoHud(target, mod, delta);
            case RAVEN -> this.drawRavenHud(target, mod, delta);
        }

    }

    private void drawDetailedHud(EntityLivingBase target, HudMod mod, float delta) {
        float health = target.getHealth() + target.getAbsorptionAmount();
        float maxHealth = target.getMaxHealth() + target.getAbsorptionAmount();
        int color = this.getColor(target, mod);

        String hurt = target.hurtTime <= 0 ? "\247aDamageable" : "\247cImmune \2477(\247f" + target.hurtTime + "\2477)";
        String distance = "Distance: " + DISTANCE_FORMAT.format(mc.thePlayer.getDistanceToEntity(target));

        this.translate.interpolate((health / maxHealth) * 350.0F, 0.0F, 0.2F, delta);

        this.setSize(360, 150);

        NVGHelper.begin();
        this.nvgTranslate();
        this.nvgDrawBackground(0x80000000);
        NVGHelper.drawRect(5.0F, 135.0F, 350.0F, 10.0F, ColorUtil.dropShadow(color));
        NVGHelper.drawRect(5.0F, 135.0F, this.translate.getX(), 10.0F, color);
        NVGHelper.end();

        RenderUtil.glColor(ColorUtil.WHITE);
        GuiInventory.drawEntityOnScreen(40, 127, 60, -80, 0, target);

        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0F, 2.0F, 1.0F);
        int itemY = 2;
        for (int i = 3; i >= 0; i--) {
            ItemStack stack = target.getCurrentArmor(i);
            if (stack != null) {
                mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 162, itemY);
                itemY += 16;
            }
        }

        ItemStack currentItem = target.getHeldItem();
        if (currentItem != null) {
            mc.getRenderItem().renderItemIntoGUI(currentItem, 146, 2);
        }

        GlStateManager.popMatrix();

        RenderUtil.drawScaledString(target.getName(), 82.0F, 10.0F, ColorUtil.WHITE, true, 2.0F);
        RenderUtil.drawScaledString(ASTOLFO_FORMAT.format(health), 80.0F, 34.0F, color, true, 4.0F);
        RenderUtil.drawScaledString(hurt, 82.0F, 80.0F, ColorUtil.WHITE, true, 2.0F);
        RenderUtil.drawScaledString(distance, 82.0F, 104.0F, ColorUtil.WHITE, true, 2.0F);
    }

    private void drawAstolfoHud(EntityLivingBase target, HudMod mod, float delta) {

        float health = target.getHealth() + target.getAbsorptionAmount();
        float maxHealth = target.getMaxHealth() + target.getAbsorptionAmount();
        int color = this.getColor(target, mod);

        this.translate.interpolate((health / maxHealth) * 220.0F, 0.0F, 0.2F, delta);

        this.setSize(300, 100);

        NVGHelper.begin();
        this.nvgTranslate();
        this.nvgDrawBackground(0x80000000);
        NVGHelper.drawRect(72.0F, 72.0F, 220.0F, 18.0F, ColorUtil.dropShadow(color));
        NVGHelper.drawRect(72.0F, 72.0F,  this.translate.getX(), 18.0F, color);
        NVGHelper.end();

        RenderUtil.glColor(ColorUtil.WHITE);
        GuiInventory.drawEntityOnScreen(32, 90, 40, -80, 0, target);

        RenderUtil.drawScaledString(target.getName(), 72.0F, 10.0F, ColorUtil.WHITE, true, 2.0F);
        RenderUtil.drawScaledString(ASTOLFO_FORMAT.format(health), 70.0F, 34.0F, color, true, 4.0F);

    }

    private void drawRavenHud(EntityLivingBase target, HudMod mod, float delta) {

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

        this.translate.interpolate((this.getWidth() - 50.0F) * (target.getHealth() / target.getMaxHealth()), 0.0F, 0.2F, delta);

        int color;
        int color2;
        switch (mod.targetHudColorMode.get()) {
            case STATIC -> {
                if (Hud.enableSecondary()) {
                    color = Hud.color();
                    color2 = Hud.secondary();
                } else {
                    color = Hud.color();
                    color2 = ColorUtil.multiplySatBri(color, 0.5F, 2.0F);
                }
            }
            case HEALTH -> {
                color = target.getAbsorptionAmount() != 0.0F ? 0xFFFFAA00 : ColorUtil.health(target);
                color2 = ColorUtil.multiplySatBri(color, 0.5F, 2.0F);
            }
            default -> color = color2 = ColorUtil.WHITE;
        }

        NVGHelper.begin();
        this.nvgTranslate();

        // Background
        NVGHelper.drawRoundedRect(0.0F, 0.0F, this.getWidth(), this.getHeight(), 10.0F, 0x80000000);

        // Health bar
        NVGHelper.drawRoundedRect(20.0F, 50.0F, this.getWidth() - 40.0F, 10.0F, 5.0F, 0x80000000);
        NVGHelper.beginPath();
        NVGHelper.roundedRect(20.0F, 50.0F, 10.0F + this.translate.getX(), 10.0F, 5.0F);
        NVGHelper.fillPaint(NVGHelper.linearGradient(20.0F, this.getHeight() / 2.0F, 30.0F + this.translate.getX(), this.getHeight() / 2.0F, color, color2));
        NVGHelper.fill();
        NVGHelper.closePath();

        // Outline
        NVGHelper.beginPath();
        NVGHelper.roundedRect(0.0F, 0.0F, this.getWidth(), this.getHeight(), 10.0F);
        NVGHelper.strokePaint(NVGHelper.linearGradient(0.0F, this.getHeight() / 2.0F, this.getWidth(), this.getHeight() / 2.0F, color, color2));
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

    private void onModeChange(HudMod.TargetHud oldValue, HudMod.TargetHud value) {
        translate.setX(0.0F);
    }

}
