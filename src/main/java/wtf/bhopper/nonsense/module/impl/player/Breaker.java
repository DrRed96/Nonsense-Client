package wtf.bhopper.nonsense.module.impl.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.component.impl.player.RotationsComponent;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.packet.EventSendPacket;
import wtf.bhopper.nonsense.event.impl.player.EventUpdate;
import wtf.bhopper.nonsense.event.impl.player.interact.EventClickAction;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.minecraft.player.RotationUtil;
import wtf.bhopper.nonsense.util.minecraft.world.BlockUtil;
import wtf.bhopper.nonsense.util.misc.MathUtil;

@ModuleInfo(name = "Breaker",
        description = "Automatically breaks specific blocks.",
        category = ModuleCategory.PLAYER,
        searchAlias = {"Nuker", "Fucker" /* Yes, some clients actually call it "Fucker" (Astolfo and Future :eyes:) */ })
public class Breaker extends Module {

    private final EnumProperty<TargetBlock> targetBlock = new EnumProperty<>("Target Block", "Block to break", TargetBlock.BED);
    private final NumberProperty range = new NumberProperty("Range", "Break range", 5.0, 1.0, 6.0, 0.1, NumberProperty.FORMAT_DISTANCE);
    private final EnumProperty<Rotate> rotate = new EnumProperty<>("Rotate", "Rotations", Rotate.ALWAYS);

    private MovingObjectPosition mouseOver = null;
    private boolean isBreaking = false;
    private boolean didBreak = false;

    public Breaker() {
        this.addProperties(this.targetBlock, this.range, this.rotate);
    }

    @Override
    public void onEnable() {
        this.mouseOver = null;
        this.isBreaking = false;
        this.didBreak = false;
    }

    @EventLink
    public final Listener<EventClickAction> onClick = event -> {
        switch (this.targetBlock.get()) {
            case BED -> {
                this.mouseOver = null;

                BlockPos[] bed = this.findBed();

                if (bed == null) {
                    return;
                }

                this.mouseOver = this.getMouseOver(bed);

                if (this.mouseOver == null) {
                    return;
                }

                if (!mc.playerController.isHittingBlock()) {
                    event.left = true;
                    event.mouseOver = this.mouseOver;
                    event.blockClick = true;
                    event.blockClickTarget = this.mouseOver;
                    this.isBreaking = true;

                } else if (this.isBreaking) {
                    event.blockClick = true;
                    event.blockClickTarget = this.mouseOver;
                }

            }

            case DRAGON_EGG -> {
                this.mouseOver = null;
            }
        }
    };

    @EventLink
    public final Listener<EventSendPacket> onSend = event -> {
        if (event.packet instanceof C07PacketPlayerDigging packet) {
            switch (packet.getStatus()) {
                case STOP_DESTROY_BLOCK -> {
                    this.isBreaking = false;
                    this.didBreak = packet.getPosition() == this.mouseOver.getBlockPos();
                }
                case ABORT_DESTROY_BLOCK -> this.isBreaking = false;
            }
        }
    };

    @EventLink
    public final Listener<EventUpdate> onUpdate = _ -> {
        switch (this.rotate.get()) {
            case ALWAYS -> {
                if (this.mouseOver != null) {
                    RotationsComponent.updateServerRotations(RotationUtil.getRotations(this.mouseOver.hitVec));
                }
            }
            case ON_BREAK -> {
                if (this.mouseOver != null && this.didBreak) {
                    this.didBreak = false;
                    RotationsComponent.updateServerRotations(RotationUtil.getRotations(this.mouseOver.hitVec));
                }
            }

        }
    };

    private MovingObjectPosition getMouseOver(BlockPos... blocks) {
        for (BlockPos pos : blocks) {
            Block block = BlockUtil.getBlock(pos);
            Vec3 start = PlayerUtil.eyesPos();
            Vec3 end = MathUtil.centrePoint(block.getSelectedBoundingBox(mc.theWorld, pos));
            MovingObjectPosition intercept = block.collisionRayTrace(mc.theWorld, pos, start, end);

            if (intercept == null) {
                continue;
            }

            if (start.distanceTo(intercept.hitVec) <= this.range.getFloat()) {
                return intercept;
            }
        }

        return null;
    }

    private BlockPos[] findBed() {
        int rangeI = (int) Math.ceil(this.range.get() + 2);
        for (int x = -rangeI; x <= rangeI; x++) {
            for (int y = -rangeI; y <= rangeI; y++) {
                for (int z = -rangeI; z <= rangeI; z++) {
                    BlockPos pos = new BlockPos(mc.thePlayer.getPositionEyes(1.0F)).add(x, y, z);
                    IBlockState state = mc.theWorld.getBlockState(pos);
                    Block block = state.getBlock();

                    if (block == Blocks.bed && state.getValue(BlockBed.PART) == BlockBed.EnumPartType.FOOT) {
                        BlockPos other = pos.offset(state.getValue(BlockDirectional.FACING));

                        // Only return true if the other block is also a bed because only finding one could indicate the
                        // block has already been broken, it will be replaced on Hypixel.
                        if (BlockUtil.getBlock(other) == Blocks.bed) {
                            if (this.validateRange(pos, other)) {
                                return new BlockPos[]{pos, other};
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private BlockPos findEgg() {
        int rangeI = (int) Math.ceil(this.range.get());
        for (int x = -rangeI; x <= rangeI; x++) {
            for (int y = -rangeI; y <= rangeI; y++) {
                for (int z = -rangeI; z <= rangeI; z++) {
                    BlockPos pos = new BlockPos(mc.thePlayer.getPositionEyes(1.0F)).add(x, y, z);
                    IBlockState state = mc.theWorld.getBlockState(pos);
                    Block block = state.getBlock();


                    if (block == Blocks.dragon_egg) {
                        if (this.validateRange(pos)) {
                            return pos;
                        }
                    }


                }
            }
        }

        return null;
    }

    public boolean validateRange(BlockPos... blocks) {
        for (BlockPos blockPos : blocks) {
            AxisAlignedBB box = BlockUtil.getBlock(blockPos).getSelectedBoundingBox(mc.theWorld, blockPos);
            if (RotationUtil.rayCastRange(box) <= this.range.getFloat()) {
                return true;
            }
        }

        return false;
    }

    private enum TargetBlock {
        BED,
        DRAGON_EGG
    }

    private enum Rotate {
        ALWAYS,
        ON_BREAK,
        NONE
    }

    private enum BreakDefense {
        NORMAL,
        RAY_CAST,
        NONE
    }

}
