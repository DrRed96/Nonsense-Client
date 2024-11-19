package wtf.bhopper.nonsense.module.impl.movement;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.*;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.util.minecraft.*;

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
    private final EnumProperty<RotationsHitVec> rotationsHitVec = new EnumProperty<>("Hit Vector", "Block placement vector.", RotationsHitVec.CENTRE);

    private final BooleanProperty swing = new BooleanProperty("Swing", "Swings client sided.", true);
    private final EnumProperty<SwapMode> swap = new EnumProperty<>("Swap", "Swaps mode.", SwapMode.SILENT);

    private BlockData blockData = null;
    private Vec3 hitVec = null;
    private Rotation rotations = null;
    private int slot = -1;

    public Scaffold() {
        this.rotationGroup.addProperties(this.rotationsMode, this.rotationsHitVec);
        this.addProperties(this.mode, this.rotationGroup, this.swing, this.swap);
        this.setSuffix(this.mode::getDisplayValue);
    }

    @Override
    public void onEnable() {
        this.blockData = null;
        this.hitVec = null;
        this.rotations = null;
        this.slot = -1;
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

            this.hitVec = switch (this.rotationsHitVec.get()) {
                case CENTRE -> RotationUtil.getHitVec(this.blockData.blockPos, this.blockData.facing);
                case CLOSEST -> RotationUtil.getHitVecOptimized(this.blockData.blockPos, this.blockData.facing);
            };

            this.placeBlock();
        }
    };

    @EventLink
    public final Listener<EventPreMotion> onPre = event -> {

        switch (this.rotationsMode.get()) {
            case INSTANT -> {
                if (this.hitVec != null) {
                    this.rotations = RotationUtil.getRotations(this.hitVec);
                    this.hitVec = null;
                }
            }

            case PLACE -> {
                if (this.hitVec != null) {
                    this.rotations = RotationUtil.getRotations(this.hitVec);
                    this.hitVec = null;
                } else {
                    this.rotations = null;
                }
            }
        }

        if (this.hitVec != null) {
            this.rotations = RotationUtil.getRotations(this.hitVec);
            this.hitVec = null;
        }

        if (this.rotations != null) {
            event.setRotations(this.rotations);
        }
    };

    @EventLink
    public final Listener<EventClickAction> onClick = event -> {
        if (this.blockData != null) {
            event.cancel();
        }
    };

    public void placeBlock() {

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
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
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
            if (stack == null || !isValid(stack)) continue;
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

    private record BlockData(BlockPos blockPos, EnumFacing facing) { }

    private enum Mode {
        VANILLA
    }

    private enum RotationsMode {
        INSTANT,
        PLACE
    }

    private enum RotationsHitVec {
        CENTRE,
        CLOSEST
    }

    private enum SwapMode {
        CLIENT,
        SILENT,
        SPOOF
    }

}
