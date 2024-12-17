package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventRender3D;
import wtf.bhopper.nonsense.event.impl.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.PropertyContainer;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.ColorProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.misc.Clock;
import wtf.bhopper.nonsense.util.render.RenderUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "Block Overlay", description = "Modify block overlay", category = ModuleCategory.VISUAL)
public class BlockOverlay extends Module {

    private static final float BED_HEIGHT = 0.5625F;

    public final BlockOverlayProperty mouseOver = new BlockOverlayProperty("Mouse over", true, 0xFF0000, true, true, this);
    private final BlockOverlayProperty chests = new BlockOverlayProperty("Chest", true, 0xFFAA00, this);
    private final BlockOverlayProperty trappedChest = new BlockOverlayProperty("Trapped Chest", true, 0xFF5555, this);
    private final BlockOverlayProperty enderChests = new BlockOverlayProperty("Ender Chest", true, 0xAA00AA, this);
    private final BlockOverlayProperty hopper = new BlockOverlayProperty("Hopper", false, 0x777777, this);
    private final BlockOverlayProperty dispenser = new BlockOverlayProperty("Dispenser", false, 0x00FFFF, this);
    private final BlockOverlayProperty dropper = new BlockOverlayProperty("Dropper", false, 0x55FF55, this);
    private final BlockOverlayProperty bedSet = new BlockOverlayProperty("Bed", true, 0x55AAFF, this);
    private final BlockOverlayProperty jukeBox = new BlockOverlayProperty("Juke Box", false, 0xFF55FF, this);
    private final NumberProperty searchRange = new NumberProperty("Search Range", "Search range for non tile blocks", 30, 5, 100, 5);

    private final Clock searchTimer = new Clock();
    private final List<AxisAlignedBB> beds = new ArrayList<>();
    private final List<BlockPos> jukeBoxes = new ArrayList<>();

    public BlockOverlay() {
        this.addProperties(this.mouseOver, this.chests, this.enderChests, this.hopper, this.dispenser, this.dropper, this.bedSet, this.jukeBox, this.searchRange);
    }

    @EventLink
    public final Listener<EventRender3D> onRender3D = _ -> {

        for (TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
            try {
                if (!RenderUtil.isInViewFrustum(tileEntity)) {
                    continue;
                }
            } catch (IllegalArgumentException ignored) {
                continue;
            }

            Block type = tileEntity.getBlockType();

            if (type == Blocks.chest) {
                if (this.chests.isEnabled()) {
                    this.chests.draw(tileEntity.getPos());
                }

            } else if (type == Blocks.trapped_chest) {
                if (this.trappedChest.isEnabled()) {
                    this.trappedChest.draw(tileEntity.getPos());
                }

            } else if (type == Blocks.ender_chest) {
                if (this.enderChests.isEnabled()) {
                    this.enderChests.draw(tileEntity.getPos());
                }

            } else if (type == Blocks.hopper) {
                if (this.hopper.isEnabled()) {
                    this.hopper.draw(tileEntity.getPos());
                }

            } else if (type == Blocks.dispenser) {
                if (this.dispenser.isEnabled()) {
                    this.dispenser.draw(tileEntity.getPos());
                }

            } else if (type == Blocks.dropper) {
                if (this.dropper.isEnabled()) {
                    this.dropper.draw(tileEntity.getPos());
                }

            }


        }

        if (this.bedSet.isEnabled()) {
            for (AxisAlignedBB box : this.beds) {
                bedSet.draw(box);
            }
        }

        if (this.jukeBox.isEnabled()) {
            for (BlockPos blockPos : this.jukeBoxes) {
                this.jukeBox.draw(blockPos);
            }
        }

        if (this.mouseOver.isEnabled()) {
            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                this.mouseOver.draw(mc.objectMouseOver.getBlockPos());
            }
        }

    };

    @EventLink
    public final Listener<EventTick> onTick = _ -> {

        if (!PlayerUtil.canUpdate()) {
            return;
        }

        if (this.bedSet.isEnabled() || this.jukeBox.isEnabled()) {
            if (searchTimer.hasReached(1000)) { // Only search every 1000ms (or 1 second)
                this.beds.clear();
                for (int x = -searchRange.getInt(); x <= searchRange.getInt(); x++) {
                    for (int y = -searchRange.getInt(); y <= searchRange.getInt(); y++) {
                        for (int z = -searchRange.getInt(); z <= searchRange.getInt(); z++) {

                            BlockPos pos = mc.thePlayer.getPosition().add(x, y, z);
                            IBlockState foot = mc.theWorld.getBlockState(pos);
                            if (foot.getBlock() == Blocks.bed && foot.getValue(BlockBed.PART) == BlockBed.EnumPartType.FOOT) {
                                EnumFacing direction = foot.getValue(BlockDirectional.FACING);
                                BlockPos other = pos.offset(direction);
                                IBlockState head = mc.theWorld.getBlockState(other);

                                double footX = pos.getX();
                                double footY = pos.getY();
                                double footZ = pos.getZ();

                                if (head.getBlock() == Blocks.bed && head.getValue(BlockBed.PART) == BlockBed.EnumPartType.HEAD) {

                                    double headX = other.getX();
                                    double headZ = other.getZ();

                                    AxisAlignedBB box;
                                    if (footX != headX) {
                                        if (footX > headX) {
                                            box = new AxisAlignedBB(footX - 1.0, footY, footZ, footX + 1.0, footY + BED_HEIGHT, footZ + 1.0);
                                        } else {
                                            box = new AxisAlignedBB(footX, footY, footZ, footX + 2.0, footY + BED_HEIGHT, footZ + 1.0);
                                        }
                                    } else if (footZ > headZ) {
                                        box = new AxisAlignedBB(footX, footY, footZ - 1.0, footX + 1.0, footY + BED_HEIGHT, footZ + 1.0);
                                    } else {
                                        box = new AxisAlignedBB(footX, footY, footZ, footX + 1.0, footY + BED_HEIGHT, footZ + 2.0);
                                    }
                                    this.beds.add(box);
                                } else {
                                    this.beds.add(new AxisAlignedBB(footX, footY, footZ, footX + 1.0, footY + BED_HEIGHT, footZ + 1.0));
                                }
                            } else if (foot.getBlock() == Blocks.jukebox) {
                                this.jukeBoxes.add(pos);
                            }

                        }
                    }
                }

                this.searchTimer.reset();
            }
        } else {
            this.beds.clear();
        }

    };

    public static class BlockOverlayProperty extends GroupProperty {

        private final BooleanProperty enable;
        private final ColorProperty color;
        private final BooleanProperty box;
        private final BooleanProperty outline;

        public BlockOverlayProperty(String displayName, boolean enabled, int color, boolean box, boolean outline, PropertyContainer owner) {
            super(displayName, displayName, owner);
            this.enable = new BooleanProperty("Enable", "Enable " + displayName, enabled);
            this.color = new ColorProperty("Color", displayName + " color", color | 0xFF000000);
            this.box = new BooleanProperty("Box", displayName + " box", box);
            this.outline = new BooleanProperty("Outline", displayName + " outline", outline);
            this.addProperties(this.enable, this.color, this.box, this.outline);
        }

        public BlockOverlayProperty(String displayName, boolean enabled, int color, PropertyContainer owner) {
            this(displayName, enabled, color, true, false, owner);
        }

        public boolean isEnabled() {
            return this.enable.get();
        }

        public void draw(BlockPos blockPos) {
            RenderUtil.drawBlockBox(blockPos, this.color.get(), this.outline.get(), this.box.get(), 1.0F, false);
        }

        public void draw(AxisAlignedBB box) {
            this.draw(box, this.color.get());
        }

        public void draw(AxisAlignedBB box, Color color) {
            RenderUtil.drawAxisAlignedBB(RenderUtil.toRender(box), color, this.outline.get(), this.box.get(), 1.0F);
        }

    }

}
