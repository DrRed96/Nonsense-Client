package wtf.bhopper.nonsense.module.impl.movement;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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
import wtf.bhopper.nonsense.module.property.annotations.DisplayName;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.util.minecraft.*;
import wtf.bhopper.nonsense.util.misc.Clock;

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

    private final GroupProperty towerGroup = new GroupProperty("Tower", "Scaffold tower");
    private final BooleanProperty towerEnable = new BooleanProperty("Enable", "Enables tower", true);
    private final EnumProperty<TowerMode> towerMode = new EnumProperty<>("Mode", "Tower mode", TowerMode.VANILLA);

    private final BooleanProperty swing = new BooleanProperty("Swing", "Swings client sided.", true);
    private final EnumProperty<SwapMode> swap = new EnumProperty<>("Swap", "Swaps mode.", SwapMode.SILENT);

    private BlockData blockData = null;
    private Vec3 hitVec = null;
    private Rotation rotations = null;
    private int slot = -1;

    private int towerStage = 0;
    private boolean spoofGround = false;
    private final Clock towerTimer = new Clock();

    public Scaffold() {
        this.rotationGroup.addProperties(this.rotationsMode, this.rotationsHitVec);
        this.towerGroup.addProperties(this.towerEnable, this.towerMode);
        this.addProperties(this.mode, this.rotationGroup, this.towerGroup, this.swing, this.swap);
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

                case VERUS -> {
                    if (mc.gameSettings.keyBindJump.isKeyDown() && this.slot != -1 && BlockUtil.getBlockRelativeToPlayer(0, -1, 0).getMaterial() != Material.air && mc.thePlayer.ticksExisted % 2 == 0) {
                        MoveUtil.vertical(event, 0.42);
                    }
                }

            }
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

    private record BlockData(BlockPos blockPos, EnumFacing facing) {
    }

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

    private enum TowerMode {
        VANILLA,
        @DisplayName("NCP") NCP,
        VERUS
    }

    private enum SwapMode {
        CLIENT,
        SILENT,
        SPOOF
    }

}
