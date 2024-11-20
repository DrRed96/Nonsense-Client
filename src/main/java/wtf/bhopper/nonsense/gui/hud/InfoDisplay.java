package wtf.bhopper.nonsense.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.MathHelper;
import org.lwjglx.opengl.Display;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.impl.visual.HudMod;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;
import wtf.bhopper.nonsense.util.minecraft.MoveUtil;
import wtf.bhopper.nonsense.util.render.Fonts;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.*;

public class InfoDisplay implements MinecraftInstance {

    private final List<Component> components = new ArrayList<>();

    public void draw() {

        if (!Hud.enabled() || mc.currentScreen instanceof GuiChat) {
            return;
        }

        int width = Display.getWidth();
        int height = Display.getHeight();
        HudMod mod = Hud.mod();

        this.components.clear();

        if (mod.coords.get()) {
            this.components.add(new Component("XYZ", String.format("%,.1f, %,.1f, %,.1f", mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)));
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
            double speed = MoveUtil.getSpeed() * mc.timer.timerSpeed;
            this.components.add(new Component("Speed", switch (mod.speed.get()) {
                case MPS -> String.format("%.2f m/s", speed * 20.0);
                case KMPH -> String.format("%.2f Km/h", speed * 72.0);
                case MPH -> String.format("%.2f mps", speed * 44.74);
                default -> String.format("%.2f", speed);
            }));
        }

        if (mod.tps.get()) {
            this.components.add(new Component("TPS", String.format("%.2f", Nonsense.getTickRate().getTickRate())));
        }

        if (mod.fps.get()) {
            this.components.add(new Component("FPS", String.format("%d", Minecraft.getDebugFPS())));
        }

        NVGHelper.begin();

        NVGHelper.fontFace(Fonts.ARIAL);
        NVGHelper.fontSize(mod.fontSize.getFloat());
        NVGHelper.textAlign(NVG_ALIGN_LEFT | NVG_ALIGN_BOTTOM);

        float yOff = height - 2.0F;
        for (Component component : this.components) {
            float valueX = NVGHelper.textBounds(2.0F, yOff, component.name, new float[4]) + 1.0F;
            NVGHelper.drawText(component.name, 2.0F, yOff, mod.color.getRGB(), true);
            NVGHelper.drawText(": " + component.value, valueX, yOff, 0xFFFFFFFF, true);
            yOff -= mod.fontSize.getFloat() + 2.0F;
        }

        NVGHelper.end();
    }

    private record Component(String name, String value) {}

}
