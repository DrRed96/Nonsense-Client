package wtf.bhopper.nonsense.gui.hud;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.render.EventRenderGui;
import wtf.bhopper.nonsense.gui.components.RenderComponent;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationManager;
import wtf.bhopper.nonsense.module.impl.visual.HudMod;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;
import wtf.bhopper.nonsense.util.misc.InputUtil;
import wtf.bhopper.nonsense.util.render.NVGHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Hud implements MinecraftInstance {

    // Separate variable for loading the components since the HUD won't have been loaded yet
    private static final List<RenderComponent> componentsToAdd = new ArrayList<>();

    public final HudMod module;
    public final ModuleList moduleList = new ModuleList();
    public final Watermark watermark = new Watermark();
    public final TabGui tabGui = new TabGui();
    public final InfoDisplay infoDisplay = new InfoDisplay();
    public final NotificationManager notifications = new NotificationManager();
    public final TargetHud targetHud = new TargetHud();
    private final List<RenderComponent> components = new CopyOnWriteArrayList<>();

    public Hud() {
        this.module = Nonsense.module(HudMod.class);
        this.moduleList.init();
        this.components.addAll(componentsToAdd);
        componentsToAdd.clear();
        this.components.add(this.targetHud);
        Nonsense.getEventBus().subscribe(this);
        Nonsense.getEventBus().subscribe(this.tabGui);
    }

    @EventLink
    public final Listener<EventRenderGui> onRenderGui = _ -> this.targetHud.setEnabled(enabled() && mod().targetHudEnabled.get());

    public static void addComponent(RenderComponent component) {
        if (Nonsense.getHud() == null) {
            componentsToAdd.add(component);
        } else {
            Nonsense.getHud().components.add(component);
        }
    }

    public void drawComponents(ScaledResolution scaledRes, float delta, boolean mouse, boolean drawOutline) {
        GlStateManager.pushMatrix();
        scaledRes.scaleToFactor(1.0F);
        int[] mousePos = mouse ? InputUtil.getUnscaledMousePositions() : new int[]{ -1, -1 };

        for (RenderComponent component : components) {
            if (component.isEnabled()) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(component.getX(), component.getY(), 0.0);
                component.draw(delta, mousePos[0], mousePos[1], drawOutline);
                GlStateManager.popMatrix();
                if (drawOutline) {
                    component.drawOutline();
                }
            }
        }

        GlStateManager.popMatrix();
    }

    public void componentsClick(int button) {
        int[] mousePos = InputUtil.getUnscaledMousePositions();
        for (RenderComponent component : components) {
            if (component.isEnabled()) {
                component.onClick(mousePos[0] - component.getX(), mousePos[1] - component.getY(), button);
            }
        }
    }

    public List<RenderComponent> getComponents() {
        return this.components;
    }

    public static HudMod mod() {
        try {
            return Nonsense.getHud().module;
        } catch (NullPointerException ignored) {}
        return Nonsense.module(HudMod.class);
    }

    public static int color() {
        return mod().color.getRGB();
    }

    public static boolean enabled() {
        return mod().isToggled() && (!mod().hideInF3.get() || !mc.gameSettings.showDebugInfo);
    }

    public static void bindFont() {
        NVGHelper.fontFace(mod().font.get().font);
    }

    public interface WidthMethod {
        float getWidth(String text);
    }

}
