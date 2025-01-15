package wtf.bhopper.nonsense.module.impl.other;

import net.minecraft.entity.player.EnumPlayerModelParts;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.misc.Stopwatch;

import java.util.Set;

@ModuleInfo(name = "Skin Blinker",
        description = "Makes your skin blink",
        category = ModuleCategory.OTHER,
        hidden = true)
public class SkinBlinker extends Module {

    public final NumberProperty delay = new NumberProperty("Delay", "Delay between changing", 250, 50, 1000, 50, NumberProperty.FORMAT_MS);

    private final Stopwatch timer = new Stopwatch();

    public SkinBlinker() {
        super();
        this.addProperties(this.delay);
    }

    @EventLink
    public final Listener<EventTick> onTick = _ -> {
        if (timer.hasReached(this.delay.get())) {
            this.timer.reset();
            Set<EnumPlayerModelParts> activeParts = mc.gameSettings.getModelParts();
            for (EnumPlayerModelParts part : EnumPlayerModelParts.values()) {
                mc.gameSettings.setModelPartEnabled(part, !activeParts.contains(part));
            }
        }
    };

}
