package wtf.bhopper.nonsense.gui.hud;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.lwjglx.opengl.Display;
import wtf.bhopper.nonsense.component.TickRateComponent;
import wtf.bhopper.nonsense.module.impl.visual.HudMod;
import wtf.bhopper.nonsense.util.minecraft.IMinecraft;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.render.Fonts;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BOTTOM;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;

public class InfoDisplay implements IMinecraft {

    private final List<Component> components = new ArrayList<>();
    private final List<PotionComponent> potionComponents = new ArrayList<>();

    public void drawArmor(ScaledResolution sr) {
        if (!Hud.enabled() || !PlayerUtil.canUpdate() || !Hud.mod().armorHud.get()) {
            return;
        }

        int left = sr.getScaledWidth() / 2 + 85;

        int offset = 16;
        for (ItemStack stack : mc.thePlayer.inventory.armorInventory) {
            if (stack != null) {
                int x = left - offset;
                int y = sr.getScaledHeight() - (mc.thePlayer.capabilities.isCreativeMode ? 40 : mc.thePlayer.isInsideOfMaterial(Material.water) ? 68 : 56);
                mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.bitFontRenderer, stack, x, y, null);
                offset += 18;
            }
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void draw(ScaledResolution scaledRes) {

        if (!Hud.enabled() || mc.currentScreen instanceof GuiChat) {
            return;
        }

        int width = Display.getWidth();
        int height = Display.getHeight();
        HudMod mod = Hud.mod();

        this.components.clear();
        this.potionComponents.clear();

        if (mod.coords.get()) {
            this.components.add(new Component("XYZ", String.format("%,.1f / %,.1f / %,.1f", mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)));
        }

        if (mod.angles.get()) {
            this.components.add(new Component("Angles", String.format("(%.1f / %.1f) %s", MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw), mc.thePlayer.rotationPitch, switch (mc.thePlayer.getHorizontalFacing()) {
                case NORTH -> "[Z-] North";
                case SOUTH -> "[Z+] South";
                case EAST -> "[X-] East";
                case WEST -> "[X+] West";
                default -> "[?] ???";
            })));
        }

        if (!mod.speed.is(HudMod.Speed.NONE)) {
            double speed = Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * mc.timer.timerSpeed;
            this.components.add(new Component("Speed", switch (mod.speed.get()) {
                case MPS -> String.format("%.2f m/s", speed * 20.0);
                case KMPH -> String.format("%.2f Km/h", speed * 72.0);
                case MPH -> String.format("%.2f mph", speed * 44.74);
                default -> String.format("%.2f", speed);
            }));
        }

        if (mod.tps.get()) {
            this.components.add(new Component("TPS", String.format("%.2f", TickRateComponent.getTickRate())));
        }

        if (mod.fps.get()) {
            this.components.add(new Component("FPS", String.format("%d", Minecraft.getDebugFPS())));
        }

        if (mod.pots.get()) {

            Hud.WidthMethod getWidth = mod.font.is(HudMod.Font.MINECRAFT)
                    ? text -> Fonts.mc().getStringWidthF(text) * 2.0F
                    : NVGHelper::getStringWidth;

            for (PotionEffect effect : mc.thePlayer.getActivePotionEffects()) {
                String name = "TODO: FIX THIS FUCKING THING.";
                int color = 0xFFAAAAAA;
                try {
                    name = EnumChatFormatting.getTextWithoutFormattingCodes(I18n.format(Potion.potionTypes[effect.getPotionID()].getName()));
                    color = Potion.potionTypes[effect.getPotionID()].getLiquidColor() | 0xFF000000;
                } catch (ArrayIndexOutOfBoundsException ignored) {}
                String display = String.format("%s %d", name, effect.getAmplifier() + 1);
                String time = Potion.getDurationString(effect);
                float potWidth = getWidth.getWidth(display + " " + time);
                int effectColor;
                if (effect.getDuration() < 300) {
                    effectColor = 0xFFFF5555;
                } else if (effect.getDuration() < 600) {
                    effectColor = 0xFFFFAA00;
                } else {
                    effectColor = 0xFFAAAAAA;
                }
                this.potionComponents.add(new PotionComponent(
                        display,
                        time,
                        color,
                        effectColor,
                        potWidth,
                        potWidth - getWidth.getWidth(time)
                ));
                this.potionComponents.sort(Comparator.<PotionComponent>comparingDouble(component -> component.width).reversed());
            }
        }

        if (mod.font.is(HudMod.Font.MINECRAFT)) {
            GlStateManager.pushMatrix();
            scaledRes.scaleToFactor(2.0F);
            float yOff = height / 2.0F - 11.0F;
            for (Component component : this.components) {
                Fonts.mc().drawStringWithShadow(component.name + "\247f: " + component.value, 2.0F, yOff, mod.color.getRGB());
                yOff -= 11.0F;
            }

            yOff = height / 2.0F - 11.0F;
            for (PotionComponent component : this.potionComponents) {
                Fonts.mc().drawStringWithShadow(component.name, (width - component.width - 2.0F) / 2.0F, yOff, component.potColor);
                Fonts.mc().drawStringWithShadow(component.time, (width - component.width + component.timeOffset - 2.0F) / 2.0F, yOff, component.timeColor);
                yOff -= 11.0F;
            }

            GlStateManager.popMatrix();
        } else {
            NVGHelper.begin();

            Hud.bindFont();
            NVGHelper.fontSize(mod.fontSize.getFloat());
            NVGHelper.textAlign(NVG_ALIGN_LEFT | NVG_ALIGN_BOTTOM);

            float yOff = height - 2.0F;
            for (Component component : this.components) {
                float valueX = 2.0F + NVGHelper.getStringWidth(component.name);
                NVGHelper.drawText(component.name, 2.0F, yOff, mod.color.getRGB(), true);
                NVGHelper.drawText(": " + component.value, valueX, yOff, 0xFFFFFFFF, true);
                yOff -= mod.fontSize.getFloat() + 2.0F;
            }

            yOff = height - 2.0F;
            for (PotionComponent component : this.potionComponents) {
                NVGHelper.drawText(component.name, width - component.width - 2.0F, yOff, component.potColor, true);
                NVGHelper.drawText(component.time, width - component.width + component.timeOffset - 2.0F, yOff, component.timeColor, true);
                yOff -= mod.fontSize.getFloat() + 2.0F;
            }

            NVGHelper.end();
        }
    }

    private record Component(String name, String value) {}
    private record PotionComponent(String name, String time, int potColor, int timeColor, float width, float timeOffset) {}

}
