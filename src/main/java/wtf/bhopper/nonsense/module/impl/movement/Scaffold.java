package wtf.bhopper.nonsense.module.impl.movement;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import wtf.bhopper.nonsense.component.impl.player.RotationsComponent;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.*;
import wtf.bhopper.nonsense.event.impl.player.interact.EventClickAction;
import wtf.bhopper.nonsense.event.impl.player.inventory.EventSelectItem;
import wtf.bhopper.nonsense.event.impl.player.movement.EventMove;
import wtf.bhopper.nonsense.event.impl.player.movement.EventMovementInput;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.annotations.DisplayName;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.module.property.impl.RotationsProperty;
import wtf.bhopper.nonsense.util.minecraft.player.*;
import wtf.bhopper.nonsense.util.minecraft.world.BlockUtil;
import wtf.bhopper.nonsense.util.misc.Stopwatch;

import java.util.Arrays;
import java.util.List;

@ModuleInfo(name = "Scaffold",
        description = "Automatically places blocks under you.",
        category = ModuleCategory.MOVEMENT)
public class Scaffold extends Module {

    public static final List<Block> BAD_BLOCKS = Arrays.asList(
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

    private final EnumProperty<Mode> mode = new EnumProperty<>("Mode", "Scaffold method.", Mode.NORMAL);

    private final RotationsProperty rotationsProperty = new RotationsProperty("Rotations", "Scaffold rotations.", this);
    private final EnumProperty<FallBack> fallBack = new EnumProperty<>("Fall Back", "Use fall back rotations if optimal rotations cannot be found.", FallBack.CENTRE);

    private final GroupProperty towerGroup = new GroupProperty("Tower", "Scaffold tower", this);
    private final BooleanProperty towerEnable = new BooleanProperty("Enable", "Enables tower", true);
    private final EnumProperty<TowerMode> towerMode = new EnumProperty<>("Mode", "Tower mode", TowerMode.VANILLA);

    private final BooleanProperty swing = new BooleanProperty("Swing", "Swings client sided.", true);
    private final EnumProperty<SwapMode> swap = new EnumProperty<>("Swap", "Swaps mode.", SwapMode.SILENT);
    private final BooleanProperty sameY = new BooleanProperty("Same Y", "Keeps your Y position the same", false);
    private final BooleanProperty down = new BooleanProperty("Down", "Allows scaffold to go down by sneaking", false);

    private BlockData blockData = null;
    private Rotation rotations = null;
    private Rotation targetRotations = null;
    private Rotation prevRotations = null;
    private MovingObjectPosition mouseOver = null;

    private double playerY = -1.0F;
    private int slot = -1;

    private int towerStage = 0;
    private boolean spoofGround = false;
    private final Stopwatch towerTimer = new Stopwatch();

    public Scaffold() {
        super();
        this.rotationsProperty.addProperties(this.fallBack);
        this.towerGroup.addProperties(this.towerEnable, this.towerMode);
        this.addProperties(this.mode, this.rotationsProperty, this.towerGroup, this.swing, this.swap, this.sameY, this.down);
        this.setSuffix(this.mode::getDisplayValue);
    }

    @Override
    public void onEnable() {
        this.blockData = null;
        this.rotations = null;
        this.targetRotations = null;
        this.prevRotations = null;
        this.slot = -1;
        this.towerStage = 0;
        this.spoofGround = false;
        this.playerY = mc.thePlayer.posY;
    }

    @EventLink
    public final Listener<EventSelectItem> onSelectItem = event -> {


        this.selectBlocks(event.slot);
        if (this.slot != -1) {
            event.slot = this.slot;
            event.silent = this.swap.is(SwapMode.SILENT);
        }
    };

    @EventLink
    public final Listener<EventClickAction> onClick = event -> {

        if (this.slot == -1) {
            return;
        }

        this.blockData = this.getBlockData();
        if (this.blockData == null) {
            this.mouseOver = null;
            return;
        }

        this.calculateTargetRotations();

        this.prevRotations = this.rotations == null ? new Rotation(mc.thePlayer) : this.rotations;
        if (this.targetRotations != null) {
            this.rotations = this.rotationsProperty.rotate(this.prevRotations, this.targetRotations);
            this.mouseOver = RotationUtil.rayCastBlocks(this.rotations, 4.5, mc.thePlayer);
            if (this.mouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.MISS) {
                this.mouseOver = null;
            }
        }

        if (this.mouseOver == null || event.usingItem) {
            return;
        }

        event.right = true;
        event.mouseOver = this.mouseOver;
        event.rightSwing = this.swing.get();
    };

    @EventLink
    public final Listener<EventUpdate> onUpdate = _ -> {
        if (this.rotations != null) {
            RotationsComponent.updateServerRotations(this.rotations);
        }
    };

    @EventLink
    public final Listener<EventPreMotion> onPre = event -> {
        if (this.spoofGround) {
            event.onGround = true;
            this.spoofGround = false;
        }
    };

    @EventLink
    public final Listener<EventMove> onMove = this::tower;

    @EventLink
    public final Listener<EventMovementInput> onMovementInput = event -> {
        if (this.down.get()) {
            event.sneak = false;
        }
    };

    private BlockData getBlockData() {

        if (!this.sameY.get() || mc.thePlayer.onGround || mc.thePlayer.fallDistance >= 2.5F) {
            this.playerY = mc.thePlayer.posY;
        }

        double x = mc.thePlayer.posX;
        double y = this.playerY;
        double z = mc.thePlayer.posZ;

        BlockPos playerPos = new BlockPos(x, y, z).down();

        if (this.down.get() && mc.gameSettings.keyBindSneak.isKeyDown() && mc.thePlayer.onGround) {
            playerPos = playerPos.down();
        }

        if (BlockUtil.isAir(playerPos)) {
            for (EnumFacing face : EnumFacing.values()) {
                if (face == EnumFacing.UP && !this.down.get()) {
                    continue;
                }
                BlockPos offset = playerPos.offset(face);
                if (!BlockUtil.isAir(offset)) {
                    return new BlockData(offset, face.getOpposite());
                }
            }

            for (EnumFacing face : EnumFacing.values()) {
                if (face == EnumFacing.UP && !this.down.get()) {
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

    private void calculateTargetRotations() {

        switch (this.mode.get()) {
            case NORMAL -> {
                EntityPlayer player = mc.thePlayer;

                double diff = player.posY + player.getEyeHeight() - this.blockData.blockPos.getY() - 0.5 - (Math.random() - 0.5 * 0.1);

                MovingObjectPosition mouseOver;

                for (int offset = -180; offset <= 180; offset += 45) {
                    player.setPosition(player.posX, player.posY - diff, player.posZ);
                    mouseOver = RotationUtil.rayCastBlocks(new Rotation(player.rotationYaw + (offset * 3.0F), 0.0F), 4.5, player);
                    player.setPosition(player.posX, player.posY + diff, player.posZ);

                    if (mouseOver == null || mouseOver.hitVec == null) {
                        return;
                    }

                    Rotation newRotations = RotationUtil.getRotations(mouseOver.hitVec);

                    if (RotationUtil.isOverBlock(newRotations, this.blockData.blockPos, this.blockData.facing)) {
                        this.targetRotations = newRotations;
                        return;
                    }
                }

                if (this.targetRotations == null || !RotationUtil.isOverBlock(this.targetRotations, this.blockData.blockPos, this.blockData.facing)) {
                    switch (this.fallBack.get()) {
                        case CENTRE -> this.targetRotations = RotationUtil.getRotations(this.blockData.blockPos, this.blockData.facing);
                        case CLOSEST -> this.targetRotations = RotationUtil.getRotations(RotationUtil.getHitVecOptimized(this.blockData.blockPos, this.blockData.facing));
                    }
                }
            }

            case WATCHDOG -> {

                EntityPlayer player = mc.thePlayer;
                double diff = player.posY + player.getEyeHeight() - this.blockData.blockPos.getY() - 0.5 - (Math.random() - 0.5 * 0.1);

                for (float f : new float[]{ 51.0F, -51.0F, 0.0F }) {

                    player.setPosition(player.posX, player.posY - diff, player.posZ);
                    mouseOver = RotationUtil.rayCastBlocks(new Rotation(player.rotationYaw - 180.0F + f, 0.0F), 4.5, player);
                    player.setPosition(player.posX, player.posY + diff, player.posZ);

                    if (mouseOver == null || mouseOver.hitVec == null) {
                        continue;
                    }

                    Rotation newRotations = RotationUtil.getRotations(mouseOver.hitVec);
                    if (RotationUtil.isOverBlock(newRotations, this.blockData.blockPos, this.blockData.facing)) {
                        this.targetRotations = newRotations;
                        return;
                    }

                }

                if (this.targetRotations == null || !RotationUtil.isOverBlock(this.targetRotations, this.blockData.blockPos, this.blockData.facing)) {
                    switch (this.fallBack.get()) {
                        case CENTRE -> this.targetRotations = RotationUtil.getRotations(this.blockData.blockPos, this.blockData.facing);
                        case CLOSEST -> this.targetRotations = RotationUtil.getRotations(RotationUtil.getHitVecOptimized(this.blockData.blockPos, this.blockData.facing));
                    }
                }

//                this.targetRotations = new Rotation(mc.thePlayer.rotationYaw - 180.0f + 51.0f, 89.0f);
            }
        }

    }

    private void tower(EventMove event) {

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
    }

    private void selectBlocks(int slot) {
        if (this.isBlockSlot(slot)) {
            this.slot = slot;
            return;
        }
        this.slot = this.getHighestBlockSlot();
    }

    private int getHighestBlockSlot() {
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

    private boolean isBlockSlot(int slot) {
        ItemStack stack = mc.thePlayer.inventory.mainInventory[slot];
        return stack != null && isValid(stack) && stack.stackSize > 0;
    }

    public static boolean isValid(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ItemBlock block) {
            return !BAD_BLOCKS.contains(block.getBlock()) && block.getBlock().isNormalCube() && block.getBlock().isCollidable();
        }
        return false;
    }

    public record BlockData(BlockPos blockPos, EnumFacing facing) { }

    private enum Mode {
        NORMAL,
        WATCHDOG
    }

    private enum FallBack {
        CENTRE,
        CLOSEST,
        NONE
    }

    private enum TowerMode {
        VANILLA,
        @DisplayName("NCP") NCP,
        VERUS,
        LEGIT,

    }

    private enum SwapMode {
        CLIENT,
        SILENT
    }

}
