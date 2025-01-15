package wtf.bhopper.nonsense.module.impl.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventUpdate;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.player.PacketUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.minecraft.world.BlockUtil;
import wtf.bhopper.nonsense.util.misc.MathUtil;

@ModuleInfo(name = "Nuker",
        description = "Destroys large amounts of blocks",
        category = ModuleCategory.PLAYER)
public class Nuker extends Module {

    private final NumberProperty radius = new NumberProperty("Radius", "Block break radius", 4.5, 1.0, 6.0, 0.1);
    private final BooleanProperty packet = new BooleanProperty("Packet", "Packet mode", false);

    public Nuker() {
        super();
        this.addProperties(this.radius, this.packet);
    }

    @EventLink
    public final Listener<EventUpdate> onUpdate = _ -> {
        if (!mc.thePlayer.capabilities.isCreativeMode) {
            this.toggle(false);
            return;
        }

        Vec3 eyes = PlayerUtil.eyesPos();

        for (int x = -6; x <= 6; x++) {
            for (int y = -6; y <= 6; y++) {
                for (int z = -6; z <= 6; z++) {

                    BlockPos pos = new BlockPos(eyes).add(x, y, z);
                    Block block = BlockUtil.getBlock(pos);
                    if (block == Blocks.air || block instanceof BlockLiquid) {
                        continue;
                    }

                    AxisAlignedBB bounds = block.getSelectedBoundingBox(mc.theWorld, pos);
                    Vec3 centre = MathUtil.centrePoint(bounds);
                    MovingObjectPosition intercept = block.collisionRayTrace(mc.theWorld, pos, eyes, centre);

                    if (intercept.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                        if (intercept.hitVec.distanceTo(eyes) <= this.radius.getDouble()) {
                            if (this.packet.get()) {
                                PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, intercept.getBlockPos(), intercept.sideHit));
                            } else {
                                mc.playerController.clickBlock(intercept.getBlockPos(), intercept.sideHit);
                            }
                        }
                    }

                }
            }
        }



    };

}
