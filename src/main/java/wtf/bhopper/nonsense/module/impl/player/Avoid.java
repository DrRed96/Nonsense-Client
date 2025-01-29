package wtf.bhopper.nonsense.module.impl.player;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.world.EventBlockBounds;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;

@ModuleInfo(name = "Avoid",
        description = "Prevents you from interacting with certain blocks",
        category = ModuleCategory.PLAYER)
public class Avoid extends AbstractModule {

    private final BooleanProperty cactus = new BooleanProperty("Cactus", "Prevents you from interacting with cacti.", true);
    private final BooleanProperty lava = new BooleanProperty("Lava", "Prevents you from walking into lava.", false);
    private final BooleanProperty fire = new BooleanProperty("Fire", "Prevents you from walking into fire", false);

    public Avoid() {
        this.addProperties(this.cactus, this.lava, this.fire);
    }

    @EventLink
    public final Listener<EventBlockBounds> onBlockCollide = event -> {

        if (this.cactus.get() && event.block == Blocks.cactus) {
            event.bounds = new AxisAlignedBB(event.pos, event.pos.add(1, 1, 1));
        }

        if (this.lava.get() && (event.block == Blocks.lava || event.block == Blocks.flowing_lava)) {
            AxisAlignedBB aabb = new AxisAlignedBB(event.pos, event.pos.add(1, 2, 1));
            if (!mc.thePlayer.getEntityBoundingBox().intersectsWith(aabb.expand(0, 0.5, 0)) && !mc.thePlayer.isInLava()) {
                event.bounds = aabb;
            }
        }

        if (this.fire.get() && event.block == Blocks.fire) {
            AxisAlignedBB aabb = new AxisAlignedBB(event.pos, event.pos.add(1, 2, 1));
            if (!mc.thePlayer.getEntityBoundingBox().intersectsWith(aabb.expand(0, 0.5, 0)) && !mc.thePlayer.isInsideOfMaterial(Material.fire)) {
                event.bounds = aabb;
            }
        }
    };

}
