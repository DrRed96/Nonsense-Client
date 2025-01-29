package wtf.bhopper.nonsense.module.impl.other;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.BlockPos;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.packet.EventReceivePacket;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.util.minecraft.inventory.InventoryUtil;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.minecraft.player.RotationUtil;
import wtf.bhopper.nonsense.util.minecraft.world.BlockUtil;

@ModuleInfo(name = "Auto Pixel Party",
        description = "Plays pixel party for you",
        category = ModuleCategory.OTHER)
public class AutoPixelParty extends AbstractModule {

    private final BooleanProperty update = new BooleanProperty("Update", "Re-Updates the blocks", false);
    private final BooleanProperty debug = new BooleanProperty("Debug", "Prints debugging", false);

    private IBlockState block = null;
    private BlockPos goal = null;

    public AutoPixelParty() {
        super();
        this.addProperties(this.update, this.debug);
    }

    @Override
    public void onEnable() {
        this.goal = null;
        this.block = null;
    }

    @EventLink
    public final Listener<EventTick> onTick = _ -> {
        if (!PlayerUtil.canUpdate()) {
            return;
        }

        if (this.update.get() && this.block != null) {
            BlockPos newGoal = findNearest(this.block);
            if (newGoal != this.goal) {
                this.goal = newGoal;
                if (this.debug.get()) {
                    ChatUtil.debug("Goal: %d, %d", this.goal.getX(), this.goal.getZ());
                }
            }
        }

        if (this.goal != null) {
            mc.gameSettings.keyBindBack.setPressed(false);
            mc.gameSettings.keyBindLeft.setPressed(false);
            mc.gameSettings.keyBindRight.setPressed(false);

            if (mc.thePlayer.getPosition().getX() == this.goal.getX() && mc.thePlayer.getPosition().getZ() == this.goal.getZ()) {
                ChatUtil.debug("Goal reached!");
                mc.gameSettings.keyBindForward.setPressed(false);
                this.goal = null;
                return;
            }

            mc.thePlayer.rotationYaw = RotationUtil.getRotations(this.goal).yaw;
            mc.gameSettings.keyBindForward.setPressed(true);

        }

    };

    @EventLink
    public final Listener<EventReceivePacket> onReceivePacket = event -> {
        if (this.goal != null) {
            return;
        }

        if (event.packet instanceof S2FPacketSetSlot packet) {
            if (packet.getWindowId() == 0 && packet.getSlot() == 44) {

                if (InventoryUtil.getStack(44) == null) {
                    ItemStack item = packet.getItem();
                    if (item != null && item.getItem() instanceof ItemBlock itemBlock) {
                        this.block = itemBlock.getBlock().getStateFromMeta(item.getMetadata());

                        BlockPos newGoal = this.findNearest(this.block);
                        if (!BlockUtil.blockPosEqual(this.goal, newGoal)) {
                            this.goal = newGoal;
                            ChatUtil.debug("Goal: %d, %d", this.goal.getX(), this.goal.getZ());
                        }

                    }
                } else {
                    if (packet.getItem() == null) {
                        this.block = null;
                    }
                }

            }
        }
    };

    private BlockPos findNearest(IBlockState block) {
        BlockPos goal = null;
        for (int x = -32; x < 32; x++) {
            for (int z = -32; z < 32; z++) {
                BlockPos blockPos = new BlockPos(x, 0, z);
                IBlockState state = BlockUtil.getState(blockPos);

                try {
                    if (BlockUtil.compareStatesBasic(block, state)) {
                        if (goal == null) {
                            goal = blockPos;
                        } else {
                            double x1 = mc.thePlayer.getPosition().getX() - blockPos.getX();
                            double z1 = mc.thePlayer.getPosition().getZ() - blockPos.getZ();
                            double dist1 = Math.sqrt(x1 * x1 + z1 * z1);

                            double x2 = mc.thePlayer.getPosition().getX() - goal.getX();
                            double z2 = mc.thePlayer.getPosition().getZ() - goal.getZ();
                            double dist2 = Math.sqrt(x2 * x2 + z2 * z2);

                            if (dist1 < dist2) {
                                goal = blockPos;
                            }
                        }

                    }
                } catch (IllegalArgumentException ignored) {}
            }

        }

        return goal;
    }

}
