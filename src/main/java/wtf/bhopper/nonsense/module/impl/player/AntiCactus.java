package wtf.bhopper.nonsense.module.impl.player;

import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.world.EventBlockBounds;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;

@ModuleInfo(name = "Anti Cactus",
        description = "Prevents you from interacting with cacti",
        category = ModuleCategory.PLAYER)
public class AntiCactus extends Module {

    @EventLink
    public final Listener<EventBlockBounds> onBlockCollide = event -> {
        if (event.block == Blocks.cactus) {
            event.bounds = new AxisAlignedBB(event.pos, event.pos.add(1, 1, 1));
        }
    };

}
