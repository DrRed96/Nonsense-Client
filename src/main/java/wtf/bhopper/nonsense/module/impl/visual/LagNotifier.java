package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.client.renderer.GlStateManager;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.component.impl.world.TickRateComponent;
import wtf.bhopper.nonsense.gui.components.RenderComponent;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;

import java.text.DecimalFormat;
import java.text.NumberFormat;

@ModuleInfo(name = "Lag Notifier", description = "Notifies you about lag", category = ModuleCategory.VISUAL)
public class LagNotifier extends Module {

    private final NumberProperty threshold = new NumberProperty("Threshold", "Lag threshold", 1500, 1000, 3000, 50, NumberProperty.FORMAT_MS);

    private final Render render = new Render();

    public LagNotifier() {
        this.addProperties(this.threshold);
        this.addProperties(render.getProperties());
    }

    @Override
    public void onEnable() {
        this.render.setEnabled(true);
    }

    @Override
    public void onDisable() {
        this.render.setEnabled(false);
    }

    public class Render extends RenderComponent {

        private final NumberFormat format = new DecimalFormat("#0.0");

        public Render() {
            super("Lag Notifier", 100, 100, mc.fontRendererObj.getStringWidth("Lag Detected: 00.0") * 2, mc.fontRendererObj.FONT_HEIGHT * 2);
        }

        @Override
        public void draw(float delta, int mouseX, int mouseY, boolean bypass) {
            long timeSinceLastTick = Nonsense.component(TickRateComponent.class).timeSinceLastTickMS();

            if ((timeSinceLastTick > LagNotifier.this.threshold.get() && !mc.isSingleplayer()) || bypass) {

                int color;
                if (timeSinceLastTick > 10000) {
                    color = 0xFFFF0000;
                } else if (timeSinceLastTick > 3000) {
                    color = 0xFFFF5500;
                } else if (timeSinceLastTick > 1000) {
                    color = 0xFFFFFF00;
                } else {
                    color = 0xFF00FF00;
                }

                String text = "Lag Detected: " + format.format(timeSinceLastTick / 1000.0);

                this.setWidth(mc.fontRendererObj.getStringWidth(text) * 4);
                this.setHeight(mc.fontRendererObj.FONT_HEIGHT * 4);

                GlStateManager.pushMatrix();
                GlStateManager.scale(4.0, 4.0, 4.0);
                mc.fontRendererObj.drawStringWithShadow(text, 0, 0, color);
                GlStateManager.popMatrix();

            }
        }

    }
}
