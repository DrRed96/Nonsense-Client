package wtf.bhopper.nonsense.module.impl.movement;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.*;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.annotations.DisplayName;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.util.minecraft.*;
import wtf.bhopper.nonsense.util.misc.Clock;
import wtf.bhopper.nonsense.util.misc.MathUtil;

import java.util.Arrays;
import java.util.List;

@ModuleInfo(name = "Scaffold", description = "Automatically places blocks under you.", category = ModuleCategory.MOVEMENT)
public class Scaffold extends Module {

    private static final List<Block> BAD_BLOCKS = Arrays.asList(
            Blocks.air,
            Blocks.sand,
            Blocks.gravel,
            Blocks.hopper,
            Blocks.dropper,
            Blocks.dispenser,
            Blocks.sapling,
            Blocks.web,
            Blocks.crafting_table,
            Blocks.furnace,
            Blocks.jukebox
    );

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Scaffold method.", Mode.VANILLA);

    private final GroupProperty rotationGroup = new GroupProperty("Rotations", "Scaffold rotations.");
    private final EnumProperty<RotationsMode> rotationsMode = new EnumProperty<>("Mode", "Method for rotations.", RotationsMode.INSTANT);
    private final EnumProperty<RotationsAiming> rotationsAiming = new EnumProperty<>("Aiming", "Aiming method", RotationsAiming.HIT_VECTOR);
    private final EnumProperty<RotationsHitVec> rotationsHitVec = new EnumProperty<>("Hit Vector", "Block placement vector.", RotationsHitVec.CENTRE);
    private final BooleanProperty rotationRayCast = new BooleanProperty("Ray Cast", "Ray Cast the hit vector", false);

    private final GroupProperty towerGroup = new GroupProperty("Tower", "Scaffold tower");
    private final BooleanProperty towerEnable = new BooleanProperty("Enable", "Enables tower", true);
    private final EnumProperty<TowerMode> towerMode = new EnumProperty<>("Mode", "Tower mode", TowerMode.VANILLA);

    private final BooleanProperty swing = new BooleanProperty("Swing", "Swings client sided.", true);
    private final EnumProperty<SwapMode> swap = new EnumProperty<>("Swap", "Swaps mode.", SwapMode.SILENT);
    private final BooleanProperty sameY = new BooleanProperty("Same Y", "Keeps your Y position the same", false);

    private BlockData blockData = null;
    private Vec3 hitVec = null;
    private Rotation rotations = null;
    private Rotation prevRotations = null;

    private double playerY = -1.0F;
    private int slot = -1;

    private int towerStage = 0;
    private boolean spoofGround = false;
    private final Clock towerTimer = new Clock();

    public Scaffold() {
        this.rotationGroup.addProperties(this.rotationsMode, this.rotationsAiming, this.rotationsHitVec, this.rotationRayCast);
        this.towerGroup.addProperties(this.towerEnable, this.towerMode);
        this.addProperties(this.mode, this.rotationGroup, this.towerGroup, this.swing, this.swap, this.sameY);
        this.setSuffix(this.mode::getDisplayValue);
    }

    @Override
    public void onEnable() {
        this.blockData = null;
        this.hitVec = null;
        this.rotations = null;
        this.slot = -1;
        this.towerStage = 0;
        this.spoofGround = false;
        this.playerY = mc.thePlayer.posY;
    }

    @EventLink
    public final Listener<EventTick> onTick = event -> {
        if (PlayerUtil.canUpdate()) {
            this.selectBlocks();
            this.blockData = this.slot != -1 ? this.getBlockData() : null;
        }
    };

    @EventLink
    public final Listener<EventSelectItem> onSelectItem = event -> {
        if (this.slot != -1 && !this.swap.is(SwapMode.SPOOF)) {
            event.slot = this.slot;
            event.silent = this.swap.is(SwapMode.SILENT);
        }
    };

    @EventLink
    public final Listener<EventUpdate> onUpdate = event -> {
        if (this.blockData != null && this.slot != -1) {

            if (mc.playerController.isHittingBlock()) {
                mc.playerController.resetBlockRemoving();
                return;
            }

            BlockPos pos = this.blockData.blockPos;
            EnumFacing face = this.blockData.facing;

            this.hitVec = switch (this.rotationsHitVec.get()) {
                case CENTRE -> RotationUtil.getHitVec(pos, face);
                case CLOSEST -> RotationUtil.getHitVecOptimized(pos, face);
                case RANDOM -> {
                    double x = (double) pos.getX() + 0.5 + (double) face.getFrontOffsetX() * 0.5;
                    double y = (double) pos.getY() + 0.5 + (double) face.getFrontOffsetY() * 0.5;
                    double z = (double) pos.getZ() + 0.5 + (double) face.getFrontOffsetZ() * 0.5;

                    if (face.getAxis() != EnumFacing.Axis.Y) {
                        y += MathUtil.random(0.49, 0.5);
                    } else {
                        x += MathUtil.random(-0.3, 0.3);
                        z += MathUtil.random(-0.3, 0.3);
                    }

                    if (face.getAxis() == EnumFacing.Axis.X) {
                        z += MathUtil.random(-0.3, 0.3);
                    } else if (face.getAxis() == EnumFacing.Axis.Z) {
                        x += MathUtil.random(-0.3, 0.3);
                    }

                    yield new Vec3(x, y, z);
                }
                case CORNER -> new Vec3(pos.getX(), pos.getY(), pos.getZ());
            };

            if (this.hitVec != null && this.rotationRayCast.get()) {
                Vec3 src = PlayerUtil.eyesPos();
                MovingObjectPosition rayCast = mc.theWorld.rayTraceBlocks(src, this.hitVec, false, false, true);

                if (rayCast == null || rayCast.hitVec == null || rayCast.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
                    this.hitVec = null;
                    return;
                }

                switch (face.getAxis()) {
                    case X ->
                            rayCast.hitVec = new Vec3(Math.round(rayCast.hitVec.xCoord), rayCast.hitVec.yCoord, rayCast.hitVec.zCoord);
                    case Z ->
                            rayCast.hitVec = new Vec3(rayCast.hitVec.xCoord, rayCast.hitVec.yCoord, Math.round(rayCast.hitVec.zCoord));
                }

                if (face != EnumFacing.DOWN && face != EnumFacing.UP) {
                    final IBlockState blockState = mc.theWorld.getBlockState(rayCast.getBlockPos());
                    final Block blockAtPos = blockState.getBlock();

                    double blockFaceOffset;

                    if (blockAtPos instanceof BlockSlab && !((BlockSlab) blockAtPos).isDouble()) {
                        final BlockSlab.EnumBlockHalf half = blockState.getValue(BlockSlab.HALF);

                        blockFaceOffset = MathUtil.random(0.1, 0.4);

                        if (half == BlockSlab.EnumBlockHalf.TOP) {
                            blockFaceOffset += 0.5;
                        }
                    } else {
                        blockFaceOffset = MathUtil.random(0.1, 0.9);
                    }

                    rayCast.hitVec = rayCast.hitVec.addVector(0.0, -blockFaceOffset, 0.0);
                }

                this.hitVec = rayCast.hitVec;
            }

            this.placeBlock();
        }
    };

    @EventLink
    public final Listener<EventPreMotion> onPre = event -> {

        this.prevRotations = this.rotations;

        Rotation targetRotations = switch (this.rotationsAiming.get()) {
            case HIT_VECTOR -> this.hitVec == null ? null : RotationUtil.getRotations(this.hitVec);
        };

        this.rotations = switch (this.rotationsMode.get()) {
            case INSTANT -> targetRotations == null ? this.rotations : targetRotations;
            case PLACE -> this.blockData != null ? targetRotations : null;
        };

        if (this.rotations != null) {
            event.setRotations(this.rotations);
        }


        if (this.spoofGround) {
            event.onGround = true;
            this.spoofGround = false;
        }
    };

    @EventLink
    public final Listener<EventClickAction> onClick = event -> {
        if (this.blockData != null) {
            event.cancel();
        }
    };

    @EventLink
    public final Listener<EventMove> onMove = event -> {

        if (this.towerEnable.get()) {
            switch (this.towerMode.get()) {
                case VANILLA -> {
                    if (mc.gameSettings.keyBindJump.isKeyDown() && this.slot != -1 && BlockUtil.getBlockRelativeToPlayer(0, -1, 0).getMaterial() != Material.air) {
                        MoveUtil.vertical(event, 0.42);
                    }
                }

                case NCP -> {
                    if (mc.gameSettings.keyBindJump.isKeyDown() && this.slot != -1 && BlockUtil.getBlockRelativeToPlayer(0, -1, 0).getMaterial() != Material.air) {
                        if (this.towerTimer.hasReached(130.0 / mc.timer.timerSpeed)) {
                            MoveUtil.vertical(event, MoveUtil.jumpHeight(0.42));
                            MoveUtil.setSpeed(MoveUtil.baseSpeed() * 0.8);
                            this.towerTimer.reset();
                        } else if (towerTimer.hasReached(120.0 / mc.timer.timerSpeed)) {
                            MoveUtil.vertical(event, 0.0);
                        }
                    }
                }

                case VERUS -> {
                    if (mc.gameSettings.keyBindJump.isKeyDown() && this.slot != -1 && BlockUtil.getBlockRelativeToPlayer(0, -1, 0).getMaterial() != Material.air && mc.thePlayer.ticksExisted % 2 == 0) {
                        MoveUtil.vertical(event, 0.42);
                    }
                }

                case LEGIT -> {
                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        switch (this.towerStage) {
                            case 0 -> {
                                if (this.blockData != null && this.slot != -1 && BlockUtil.getBlockRelativeToPlayer(0, -1, 0).getMaterial() != Material.air) {
                                    MoveUtil.vertical(event, 0.42);
                                    this.towerStage = 1;
                                }
                            }
                            case 1 -> {
                                MoveUtil.vertical(event, 0.33);
                                this.towerStage = 2;
                            }
                            case 2 -> {
                                MoveUtil.vertical(event, 1.0 - mc.thePlayer.posY % 1);
                                this.towerStage = 3;
                            }
                            case 3 -> {
                                if (BlockUtil.getBlockRelativeToPlayer(0, -1, 0).getMaterial() != Material.air && this.slot != -1) {
                                    MoveUtil.vertical(event, 0.42);
                                    this.spoofGround = true;
                                } else {
                                    this.towerStage = 0;
                                }
                            }
                        }

                    } else {
                        this.towerStage = 0;
                    }
                }

            }
        }

    };

    public void placeBlock() {

        if (this.blockData == null || this.hitVec == null) {
            return;
        }

        ItemStack item = this.swap.is(SwapMode.SPOOF) ? mc.thePlayer.inventory.mainInventory[this.slot] : mc.thePlayer.getHeldItem();

        if (this.swap.is(SwapMode.SPOOF) && this.slot != mc.thePlayer.inventory.currentItem) {
            PacketUtil.send(new C09PacketHeldItemChange(this.slot));
        }

        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, item, this.blockData.blockPos, this.blockData.facing, this.hitVec)) {
            PlayerUtil.swing(!this.swing.get());
        }

        if (this.swap.is(SwapMode.SPOOF) && this.slot != mc.thePlayer.inventory.currentItem) {
            PacketUtil.send(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
    }

    private BlockData getBlockData() {

        if (!this.sameY.get() || mc.thePlayer.onGround || mc.thePlayer.fallDistance >= 2.5F) {
            this.playerY = mc.thePlayer.posY;
        }

        double x = mc.thePlayer.posX;
        double y = this.playerY;
        double z = mc.thePlayer.posZ;

        BlockPos playerPos = new BlockPos(x, y, z).down();

        if (BlockUtil.isAir(playerPos)) {
            for (EnumFacing face : EnumFacing.values()) {
                if (face == EnumFacing.UP) {
                    continue;
                }
                BlockPos offset = playerPos.offset(face);
                if (!BlockUtil.isAir(offset)) {
                    return new BlockData(offset, face.getOpposite());
                }
            }

            for (EnumFacing face : EnumFacing.values()) {
                if (face == EnumFacing.UP) {
                    continue;
                }
                BlockPos offset = playerPos.offset(face);
                if (BlockUtil.isAir(offset)) {
                    for (EnumFacing face2 : EnumFacing.values()) {
                        if (face2 == EnumFacing.UP) {
                            continue;
                        }
                        BlockPos offset2 = offset.offset(face2);
                        if (!BlockUtil.isAir(offset2)) {
                            return new BlockData(offset2, face2.getOpposite());
                        }
                    }
                }
            }
        }

        return null;
    }

    private void selectBlocks() {
        this.slot = this.getBlockSlot();
    }

    private int getBlockSlot() {
        int highestStack = -1;
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack == null || !isValid(stack)) {
                continue;
            }
            if (stack.stackSize > 0) {
                if (stack.stackSize > highestStack) {
                    highestStack = stack.stackSize;
                    slot = i;
                }
            }
        }
        return slot;
    }

    public static boolean isValid(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ItemBlock block) {
            return !BAD_BLOCKS.contains(block.getBlock()) && block.getBlock().isNormalCube() && block.getBlock().isCollidable();
        }
        return false;
    }

    public record BlockData(BlockPos blockPos, EnumFacing facing) {
    }

    private enum Mode {
        VANILLA
    }

    private enum RotationsMode {
        INSTANT,
        PLACE
    }

    private enum RotationsAiming {
        HIT_VECTOR
    }

    private enum RotationsHitVec {
        CENTRE,
        CLOSEST,
        RANDOM,
        CORNER
    }

    private enum TowerMode {
        VANILLA,
        @DisplayName("NCP") NCP,
        VERUS,
        LEGIT,

    }

    private enum SwapMode {
        CLIENT,
        SILENT,
        SPOOF
    }

}
