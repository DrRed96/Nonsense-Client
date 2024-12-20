package wtf.bhopper.nonsense.module.impl.movement;

import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.world.EventBlockBounds;
import wtf.bhopper.nonsense.event.impl.world.EventBlockCollision;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;

@ModuleInfo(name = "Terrain",
        description = "Modifies your movement on certain blocks.",
        category = ModuleCategory.MOVEMENT)
public class Terrain extends Module {

    private final EnumProperty<Mode> soulSand = new EnumProperty<>("Soul Sand", "Prevents you from being slowed by soul sand", Mode.NORMAL);
    private final EnumProperty<Mode> cobwebs = new EnumProperty<>("Cob Webs", "Prevents you from being slowed by cobwebs", Mode.NORMAL);
    private final BooleanProperty water = new BooleanProperty("Water", "Prevents you from being pushed by flowing water", true);

    public Terrain() {
        this.addProperties(this.soulSand, this.cobwebs, this.water);
    }

    @EventLink
    public final Listener<EventBlockBounds> onCollide = event -> {
        if (this.soulSand.is(Mode.COLLIDE) && event.block == Blocks.soul_sand) {
            event.bounds = new AxisAlignedBB(event.pos, event.pos.add(1, 1, 1));
        }

        if (this.cobwebs.is(Mode.COLLIDE) && event.block == Blocks.web) {
            event.bounds = new AxisAlignedBB(event.pos, event.pos.add(1, 1, 1));
        }
    };

    @EventLink
    public final Listener<EventBlockCollision> onEntityCollision = event -> {
        if (!this.soulSand.is(Mode.NONE) && event.state.getBlock() == Blocks.soul_sand) {
            event.cancel();
        }
    };

    public boolean cobwebs() {
        return this.isToggled() && !this.cobwebs.is(Mode.NONE);
    }

    public boolean water() {
        return this.isToggled() && this.water.get();
    }

    private enum Mode {
        NORMAL,
        COLLIDE,
        NONE
    }

}
