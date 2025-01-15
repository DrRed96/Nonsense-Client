package wtf.bhopper.nonsense.module.impl.other;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.component.impl.player.RotationsComponent;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.event.impl.packet.EventReceivePacket;
import wtf.bhopper.nonsense.event.impl.player.EventJoinGame;
import wtf.bhopper.nonsense.event.impl.player.inventory.EventSelectItem;
import wtf.bhopper.nonsense.event.impl.player.EventUpdate;
import wtf.bhopper.nonsense.event.impl.render.EventRenderWorld;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.ColorProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.world.BlockUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PacketUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.minecraft.player.RotationUtil;
import wtf.bhopper.nonsense.util.misc.Stopwatch;
import wtf.bhopper.nonsense.util.render.RenderUtil;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("FieldCanBeLocal")
@ModuleInfo(name = "Auto Speed Builders",
        description = "Plays speed builders for you.",
        category = ModuleCategory.OTHER)
public class AutoSpeedBuilders extends Module {

    private static final EnumFacing[] OPTIMAL_PLACE_DIRECTIONS = {
            EnumFacing.UP,
            EnumFacing.NORTH,
            EnumFacing.SOUTH,
            EnumFacing.EAST,
            EnumFacing.WEST,
            EnumFacing.DOWN
    };

    private final NumberProperty delay = new NumberProperty("Delay", "Delay between placing blocks.", 100.0, 0.0, 1000.0, 25.0, NumberProperty.FORMAT_MS);
    private final BooleanProperty disconnectedFix = new BooleanProperty("Disconnected Fix", "Tries to fix disconnected blocks.", true);

    private final GroupProperty silent = new GroupProperty("Silent", "Do things silently.", this);
    private final BooleanProperty silentSwing = new BooleanProperty("Swing", "Swing silently.", false);
    private final BooleanProperty silentSwap = new BooleanProperty("Swap", "Swap silently", false);

    private final GroupProperty render = new GroupProperty("Render", "Rendering options", this);
    private final BooleanProperty renderBuild = new BooleanProperty("Build", "Renders the build.", true);
    private final ColorProperty validBlock = new ColorProperty("Valid Block", "Color for valid blocks.", 0xFF00FF00, this.renderBuild::get);
    private final ColorProperty invalidBlock = new ColorProperty("Invalid Block", "Color for invalid blocks.", 0xFFFF0000, this.renderBuild::get);
    private final BooleanProperty renderData = new BooleanProperty("Block Data", "Renders the block data.", true);
    private final ColorProperty clickBlock = new ColorProperty("Clicked Block", "Color for the block being clicked on.", 0xFF0000FF, this.renderData::get);
    private final ColorProperty placeBlock = new ColorProperty("Placed Block", "Color for the block being placed on.", 0xFFFF00FF, this.renderData::get);

    private final Map<BlockPos, IBlockState> build = new ConcurrentHashMap<>();
    private BlockPos centrePos = null;
    private int maxY = 0;

    private boolean canBuild = false;

    private BlockData blockData;
    private Vec3 hitVec = null;

    private final Stopwatch timer = new Stopwatch();

    public AutoSpeedBuilders() {
        super();
        this.silent.addProperties(this.silentSwing, this.silentSwap);
        this.render.addProperties(this.renderBuild, this.validBlock, this.invalidBlock, this.renderData, this.clickBlock, this.placeBlock);
        this.addProperties(this.delay, this.disconnectedFix, this.silent, this.render);
    }

    @EventLink
    public final Listener<EventTick> onTick = _ -> {

        if (!PlayerUtil.canUpdate()) {
            return;
        }

        if (mc.thePlayer.capabilities.allowFlying && this.centrePos == null) {
            doneSearch:
            for (int x = -10; x <= 10; x++) {
                for (int y = 70; y < 73; y++) {
                    for (int z = -10; z <= 10; z++) {
                        BlockPos pos = new BlockPos(mc.thePlayer.posX + x, y, mc.thePlayer.posZ + z);
                        if (BlockUtil.getBlock(pos) instanceof BlockQuartz) {
                            boolean clay = true;

                            for (EnumFacing facing : new EnumFacing[]{EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST}) {
                                BlockPos offset = pos.offset(facing);
                                if (BlockUtil.getBlock(offset) instanceof BlockQuartz) {
                                    clay = false;
                                    break;
                                }
                            }

                            if (clay) {
                                this.centrePos = pos;
                                break doneSearch;
                            }
                        }

                    }
                }
            }

        }

        if (!this.canBuild || this.centrePos == null) {
            return;
        }

        try {
            this.blockData = this.getBlockData();
        } catch (NullPointerException ignored) {
            this.blockData = null;
        }

        if (this.blockData == null) {
            this.blockData = this.getBreakData();
        }

        if (this.disconnectedFix.get()) {
            if (this.blockData == null) {
                this.blockData = this.getBlockDataFix();
            }
        }
    };

    @EventLink
    public final Listener<EventSelectItem> onSelect = event -> {
        if (this.blockData != null && this.blockData.slot >= 0 && this.blockData.slot <= 8) {
            event.slot = this.blockData.slot;
            event.silent = this.silentSwap.get();
        }
    };

    @EventLink
    public final Listener<EventUpdate> onUpdate = _ -> {

        if (!this.canBuild || this.blockData == null) {
            this.hitVec = null;
            return;
        }

        if (this.blockData.hitVec == null) {
            this.hitVec = RotationUtil.getHitVec(this.blockData.pos, this.blockData.face);

            if (this.timer.hasReached(this.delay.getInt())) {
                PlayerUtil.swing(this.silentSwing.get());
                PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, this.blockData.pos, this.blockData.face));
                PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.blockData.pos, EnumFacing.DOWN));
                this.timer.reset();
            }

        } else {
            this.hitVec = this.blockData.hitVec;

            if (mc.thePlayer.getHeldItem() != null) {
                if (this.timer.hasReached(this.delay.getInt())) {
                    if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), this.blockData.pos, this.blockData.face, this.hitVec)) {
                        PlayerUtil.swing(this.silentSwing.get());
                    }
                    this.timer.reset();
                }
            }
        }

        if (this.hitVec != null) {
            RotationsComponent.updateServerRotations(RotationUtil.getRotations(this.hitVec));
        }

    };

    private BlockData getBlockData() {
        for (Map.Entry<BlockPos, IBlockState> entry : this.build.entrySet()) {
            BlockPos pos = entry.getKey();
            IBlockState targetState = entry.getValue();
            IBlockState posState = BlockUtil.getState(pos);

            if (!this.compareStates(posState, targetState, true, null).result) {
                for (EnumFacing facing : OPTIMAL_PLACE_DIRECTIONS) {

                    BlockPos offset = pos.offset(facing.getOpposite());
                    Block block = BlockUtil.getBlock(offset);

                    if (!this.validateFace(offset, facing.getOpposite(), block)) {
                        continue;
                    }

                    IBlockState itemState = null;
                    ItemStack stack = null;
                    int slot = -1;
                    for (int i = 0; i < 9; i++) {
                        stack = mc.thePlayer.inventory.mainInventory[i];
                        if (stack != null && stack.getItem() instanceof ItemBlock itemBlock) {
                            itemState = itemBlock.getBlock().getStateFromMeta(stack.getMetadata());
                            if (this.compareStates(this.build.get(pos), itemState, false, null).result) {
                                slot = i;
                                break;
                            }
                        }
                    }

                    if (stack == null || itemState == null || slot == -1) {
                        continue;
                    }

                    CheckResult check = this.compareStates(this.build.get(pos), itemState, true, new BlockData(offset, facing, null, 0));
                    if (check.result) {
                        return new BlockData(offset, facing, check.hitVec != null ? check.hitVec : RotationUtil.getHitVec(offset, facing), slot);
                    }

                }
            }
        }

        return null;
    }

    private BlockData getBreakData() {
        for (int x = -3; x <= 3; x++) {
            for (int y = 1; y <= this.maxY; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos pos = this.centrePos.add(x, y, z);
                    IBlockState block = BlockUtil.getState(pos);
                    IBlockState target = this.build.get(pos);

                    if (block.getBlock() == Blocks.air || block.getBlock() instanceof BlockLiquid) {
                        continue;
                    }

                    if (!this.compareStates(target, block, true, null).result) {
                        for (EnumFacing facing : OPTIMAL_PLACE_DIRECTIONS) {
                            if (this.validateFace(pos, facing, null)) {
                                return new BlockData(pos, facing, null, -1);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private BlockData getBlockDataFix() {
        for (Map.Entry<BlockPos, IBlockState> entry : this.build.entrySet()) {
            BlockPos targetPos = entry.getKey();
            IBlockState targetState = entry.getValue();
            Block targetBlock = targetState.getBlock();
            if (BlockUtil.getBlock(targetPos) == Blocks.air &&
                    targetBlock != Blocks.air &&
                    targetBlock != Blocks.carpet &&
                    targetBlock != Blocks.stone_button &&
                    targetBlock != Blocks.wooden_button &&
                    targetBlock != Blocks.lever &&
                    targetBlock != Blocks.trapdoor &&
                    targetBlock != Blocks.iron_trapdoor) {
                boolean surrounding = false;
                for (EnumFacing facing : OPTIMAL_PLACE_DIRECTIONS) {
                    if (BlockUtil.getBlock(targetPos.offset(facing)) != Blocks.air) {
                        surrounding = true;
                        break;
                    }
                }

                if (!surrounding) {
                    for (EnumFacing blankFace : OPTIMAL_PLACE_DIRECTIONS) {
                        BlockPos blank = targetPos.offset(blankFace);
                        if (this.validateFace(targetPos, blankFace, targetBlock)) {
                            for (EnumFacing facing : OPTIMAL_PLACE_DIRECTIONS) {
                                BlockPos offset = blank.offset(facing.getOpposite());
                                Block block = BlockUtil.getBlock(offset);
                                if (this.validateFace(offset, facing.getOpposite(), block)) {
                                    for (int i = 0; i < 9; i++) {
                                        ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
                                        if (stack != null && stack.getItem() instanceof ItemBlock itemBlock) {
                                            if (itemBlock.getBlock().isNormalCube()) {
                                                return new BlockData(offset, facing, RotationUtil.getHitVec(offset, facing), i);
                                            }
                                        }
                                    }
                                    return null;
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    @SuppressWarnings("unused")
    private boolean isBuildComplete() {
        for (int x = -3; x <= 3; x++) {
            for (int y = 1; y <= this.maxY; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos pos = this.centrePos.add(x, y, z);
                    IBlockState block = mc.theWorld.getBlockState(pos);
                    IBlockState target = this.build.get(pos);
                    if (target == null) {
                        if (block.getBlock() != Blocks.air) {
                            return false;
                        }
                        continue;
                    }

                    if (!this.compareStates(target, block, true, null).result) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void updateBuild() {
        this.build.clear();
        int y = 1;
        while (true) {
            boolean foundBlock = false;
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos pos = this.centrePos.add(x, y, z);
                    IBlockState block = mc.theWorld.getBlockState(pos);
                    if (block.getBlock() != Blocks.air) {
                        this.build.put(pos, block);
                        foundBlock = true;
                    }
                }
            }
            // the 'Question Mark' build has a space so we need to check the first 3 layers every time
            if (!foundBlock && y >= 3) {
                this.maxY = y;
                break;
            }
            y++;
        }
    }

    private boolean validateFace(BlockPos pos, EnumFacing face, Block block) {

        Vec3 eyes = PlayerUtil.eyesPos();
        Vec3 hitVec = RotationUtil.getHitVec(pos, face);

        if (eyes.distanceTo(hitVec) > 4.5) {
            // Block is out of range, this must always be done
            return false;
        }

        if (block == Blocks.air ||
                block instanceof BlockFenceGate ||
                block instanceof BlockDoor ||
                block instanceof BlockTrapDoor ||
                block instanceof BlockBush ||
                block instanceof BlockBasePressurePlate ||
                block instanceof BlockLiquid) {
            return false;
        }

        if (block instanceof BlockCarpet && face != EnumFacing.UP) {
            return false;
        }

        if (block instanceof BlockNote && !mc.thePlayer.isSneaking()) {
            return false;
        }

        double playerPos;
        double blockPos;
        switch (face.getAxis()) {
            case X -> {
                playerPos = eyes.xCoord;
                blockPos = pos.getX() + 0.5;
            }
            case Y -> {
                playerPos = eyes.yCoord;
                blockPos = pos.getY() + 0.5;
            }
            case Z -> {
                playerPos = eyes.zCoord;
                blockPos = pos.getZ() + 0.5;
            }
            default -> throw new IllegalArgumentException();

        }
        return switch (face.getAxisDirection()) {
            case POSITIVE -> playerPos < blockPos - 0.5;
            case NEGATIVE -> playerPos > blockPos + 0.5;
        };
    }

    private CheckResult compareStates(IBlockState state1, IBlockState state2, boolean place, BlockData data) {

        if (state1 == null || state2 == null) {
            return CheckResult.FALSE;
        }

        Block block1 = state1.getBlock();
        Block block2 = state2.getBlock();

        if (block1 != block2) {
            return CheckResult.FALSE;
        }

        // Some blocks have different properties that can affect their states so those need to be accounted for
        if (block1 instanceof BlockLog) {
            if (data != null) {
                return CheckResult.of(switch (state1.getValue(BlockLog.LOG_AXIS)) {
                    case X -> data.face.getAxis() == EnumFacing.Axis.X;
                    case Y -> data.face.getAxis() == EnumFacing.Axis.Y;
                    case Z -> data.face.getAxis() == EnumFacing.Axis.Z;
                    case NONE -> false;
                });
            } else if (place) {
                return CheckResult.of(BlockUtil.compareProperties(state1, state2, BlockLog.LOG_AXIS));
            } else {
                return CheckResult.TRUE;
            }

        } else if (block1 instanceof BlockStairs) {
            if (data != null) {
                EnumFacing facing = state1.getValue(BlockStairs.FACING);
                BlockStairs.EnumHalf half = state1.getValue(BlockStairs.HALF);
                Vec3 hitVec = RotationUtil.getHitVec(data.pos, data.face);

                if (facing != mc.thePlayer.getHorizontalFacing()) {
                    return CheckResult.FALSE;
                }

                switch (data.face) {
                    case UP -> {
                        if (half != BlockStairs.EnumHalf.BOTTOM) {
                            return CheckResult.FALSE;
                        }
                    }

                    case DOWN -> {
                        if (half != BlockStairs.EnumHalf.TOP) {
                            return CheckResult.FALSE;
                        }
                    }

                    default -> hitVec = hitVec.addVector(0.0, switch (half) {
                        case TOP -> 0.25;
                        case BOTTOM -> -0.25;
                    }, 0.0);

                }
                return new CheckResult(true, hitVec);
            } else if (place) {
                return CheckResult.of(BlockUtil.compareProperties(state1, state2, BlockStairs.FACING, BlockStairs.HALF));
            } else {
                return CheckResult.TRUE;
            }

        } else if (block1 instanceof BlockSlab slab) {

            if (block1 instanceof BlockWoodSlab) {
                if (!BlockUtil.compareProperties(state1, state2, BlockWoodSlab.VARIANT)) {
                    return CheckResult.FALSE;
                }
            }

            if (block1 instanceof BlockStoneSlab) {
                if (!BlockUtil.compareProperties(state1, state2, BlockStoneSlab.VARIANT)) {
                    return CheckResult.FALSE;
                }
            }

            if (data != null) {
                BlockSlab.EnumBlockHalf half = state1.getValue(BlockSlab.HALF);
                Vec3 hitVec = RotationUtil.getHitVec(data.pos, data.face);
                if (data.face.getHorizontalIndex() != -1) {
                    hitVec = hitVec.addVector(0.0, switch (half) {
                        case TOP -> 0.25;
                        case BOTTOM -> -0.25;
                    }, 0.0);
                } else {
                    if (data.face == EnumFacing.UP) {
                        if (half == BlockSlab.EnumBlockHalf.TOP) {
                            return CheckResult.FALSE;
                        }
                        return CheckResult.TRUE;
                    } else if (data.face == EnumFacing.DOWN) {
                        if (half == BlockSlab.EnumBlockHalf.BOTTOM) {
                            return CheckResult.FALSE;
                        }
                        return CheckResult.TRUE;
                    } else {
                        throw new IllegalArgumentException("HOLY SHIT THE SLAB BROKE");
                    }
                }
                return new CheckResult(true, hitVec);
            } else if (place) {
                if (!slab.isDouble()) {
                    return CheckResult.of(BlockUtil.compareProperties(state1, state2, BlockSlab.HALF));
                } else {
                    return CheckResult.FALSE;
                }
            } else {
                return CheckResult.TRUE;
            }
        } else if (block1 instanceof BlockQuartz) {
            return CheckResult.of(BlockUtil.compareProperties(state1, state2, BlockQuartz.VARIANT));
        } else if (block1 instanceof BlockColored) {
            return CheckResult.of(BlockUtil.compareProperties(state1, state2, BlockColored.COLOR));
        } else if (block1 instanceof BlockPlanks) {
            return CheckResult.of(BlockUtil.compareProperties(state1, state2, BlockPlanks.VARIANT));
        } else if (block1 instanceof BlockButton) {
            if (data != null) {
                return CheckResult.of(data.face == state1.getValue(BlockButton.FACING));
            } else if (place) {
                return CheckResult.of(BlockUtil.compareProperties(state1, state2, BlockButton.FACING));
            } else {
                return CheckResult.TRUE;
            }
        } else if (block1 instanceof BlockDispenser) {
            if (data != null) {
                return CheckResult.of(BlockPistonBase.getFacingFromEntity(mc.theWorld, data.pos, mc.thePlayer) == state1.getValue(BlockDispenser.FACING));
            } else if (place) {
                return CheckResult.of(BlockUtil.compareProperties(state1, state2, BlockDispenser.FACING));
            } else {
                return CheckResult.TRUE;
            }
        } else if (block1 instanceof BlockPumpkin || block1 instanceof BlockRedstoneDiode) {
            if (data != null) {
                return CheckResult.of(state1.getValue(BlockDirectional.FACING) == mc.thePlayer.getHorizontalFacing().getOpposite());
            } else if (place) {
                return CheckResult.of(BlockUtil.compareProperties(state1, state2, BlockDirectional.FACING));
            } else {
                return CheckResult.TRUE;
            }
        } else if (block1 instanceof BlockDirectional) {
            if (data != null) {
                return CheckResult.of(state1.getValue(BlockDirectional.FACING) == mc.thePlayer.getHorizontalFacing());
            } else if (place) {
                return CheckResult.of(BlockUtil.compareProperties(state1, state2, BlockDirectional.FACING));
            } else {
                return CheckResult.TRUE;
            }
        } else if (block1 instanceof BlockBush) {
            if (data != null) {
                return CheckResult.of(data.face == EnumFacing.UP);
            } else if (place) {
                return CheckResult.TRUE;
            } else {
                return CheckResult.TRUE;
            }
        } else if (block1 instanceof BlockLever) {
            if (data != null) {
                return CheckResult.of(state1.getValue(BlockLever.FACING) == BlockUtil.getLeverFace(data.pos, data.face));
            } else if (place) {
                return CheckResult.of(BlockUtil.compareProperties(state1, state2, BlockLever.FACING));
            } else {
                return CheckResult.TRUE;
            }
        } else if (block1 instanceof BlockStainedGlass) {
            return CheckResult.of(BlockUtil.compareProperties(state1, state2, BlockStainedGlass.COLOR));

        } else if (block1 instanceof BlockCarpet) {
            if (data != null) {
                return CheckResult.of(BlockUtil.compareProperties(state1, state2, BlockCarpet.COLOR) && data.face == EnumFacing.UP);
            } else {
                return CheckResult.of(BlockUtil.compareProperties(state1, state2, BlockCarpet.COLOR));
            }
        } else if (block1 instanceof BlockTrapDoor) {
            if (data != null) {
                if (state1.getValue(BlockTrapDoor.FACING) != data.face) {
                    return CheckResult.FALSE;
                }

                hitVec = hitVec.addVector(0.0, switch (state1.getValue(BlockTrapDoor.HALF)) {
                    case TOP -> -0.25;
                    case BOTTOM -> 0.25;
                }, 0.0);
                return new CheckResult(true, hitVec);
            } else if (place) {
                return CheckResult.of(BlockUtil.compareProperties(state1, state2, BlockTrapDoor.FACING, BlockTrapDoor.HALF));
            } else {
                return CheckResult.TRUE;
            }
        } else if (block1 instanceof BlockLadder) {
            if (data != null) {
                return CheckResult.of(data.face == state1.getValue(BlockLadder.FACING));
            } else if (place) {
                return CheckResult.of(BlockUtil.compareProperties(state1, state2, BlockLadder.FACING));
            } else {
                return CheckResult.TRUE;
            }
        } else if (block1 instanceof BlockStone) {
            return CheckResult.of(BlockUtil.compareProperties(state1, state2, BlockStone.VARIANT));
        } else if (block1 instanceof BlockSkull) {
            if (data != null) {
                return CheckResult.of(mc.thePlayer.getHorizontalFacing() == state1.getValue(BlockSkull.FACING) && data.face == EnumFacing.UP);
            } else if (place) {
                return CheckResult.of(BlockUtil.compareProperties(state1, state2, BlockSkull.FACING));
            } else {
                return CheckResult.TRUE;
            }
        }

        return CheckResult.TRUE;
    }

    @EventLink
    public final Listener<EventReceivePacket> onReceivePacket = event -> {

        if (!PlayerUtil.canUpdate()) {
            return;
        }

        if (event.packet instanceof S02PacketChat packet) {
            String message = EnumChatFormatting.getTextWithoutFormattingCodes(packet.getChatComponent().getUnformattedText());
            if (message.equals("Your builds are being judged...") || message.startsWith(mc.thePlayer.getName() + " got a perfect build in ")) {
                this.build.clear();
                this.canBuild = false;
            }
        }

        if (event.packet instanceof S45PacketTitle packet) {
            try {
                String message = EnumChatFormatting.getTextWithoutFormattingCodes(packet.getMessage().getUnformattedText());
                if (message.equalsIgnoreCase("5")) {
                    // TODO: find a different trigger, (scoreboard maybe?)
                    this.updateBuild();
                } else if (message.equalsIgnoreCase("View Time Over!")) {
                    this.canBuild = true;
                }
            } catch (NullPointerException ignored) { }
        }
    };

    @EventLink
    public final Listener<EventRenderWorld> onRender3D = _ -> {

        if (this.renderBuild.get() && !this.build.isEmpty()) {
            for (Map.Entry<BlockPos, IBlockState> entry : this.build.entrySet()) {
                Color color = this.compareStates(BlockUtil.getState(entry.getKey()), entry.getValue(), true, null).result ? this.validBlock.get() : this.invalidBlock.get();
                RenderUtil.drawBlockBox(entry.getKey(), color, true, false, 2.0F, false);
            }
        }
        if (this.renderData.get() && this.canBuild && this.blockData != null) {
            RenderUtil.drawBlockBox(this.blockData.pos, this.clickBlock.get(), true, true, 2.0F, false);
            RenderUtil.drawBlockBox(this.blockData.pos.offset(this.blockData.face), this.placeBlock.get(), true, true, 2.0F, false);
        }
    };

    @EventLink
    public final Listener<EventJoinGame> onJoin = _ -> {
        this.build.clear();
        this.blockData = null;
        this.centrePos = null;
        this.canBuild = false;
    };

    @Override
    public void onEnable() {
        this.build.clear();
        this.blockData = null;
        this.centrePos = null;
        this.canBuild = false;
    }


    private record BlockData(BlockPos pos, EnumFacing face, Vec3 hitVec, int slot) { }

    private record CheckResult(boolean result, Vec3 hitVec) {
        public static final CheckResult TRUE = new CheckResult(true, null);
        public static final CheckResult FALSE = new CheckResult(false, null);

        public static CheckResult of(boolean result) {
            return result ? TRUE : FALSE;
        }
    }



}
