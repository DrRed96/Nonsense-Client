package wtf.bhopper.nonsense.module.impl.player;

import net.minecraft.item.ItemBlock;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;

@ModuleInfo(name = "Fast Place",
        description = "Reduces/removes the delay between placing blocks.",
        category = ModuleCategory.PLAYER)
public class FastPlace extends Module {

    private final NumberProperty delay = new NumberProperty("Delay", "Delay between placing blocks in ticks.", 0, 0, 3, 1);
    private final BooleanProperty blocksOnly = new BooleanProperty("Blocks Only", "Only remove the click delay while holding blocks.", true);

    public FastPlace() {
        this.addProperties(this.delay, this.blocksOnly);
    }

    @EventLink
    public final Listener<EventTick> onTick = _ -> {
        if (PlayerUtil.canUpdate()) {

            if (this.blocksOnly.get()) {
                if (mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) {
                    return;
                }
            }

            if (mc.rightClickDelayTimer > this.delay.getInt()) {
                mc.rightClickDelayTimer = this.delay.getInt();
            }
        }
    };

}
