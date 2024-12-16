package wtf.bhopper.nonsense.module.impl.player;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventSelectItem;
import wtf.bhopper.nonsense.event.impl.EventUpdate;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.minecraft.BlockUtil;

@ModuleInfo(name = "Fast Mine", description = "Allows you to break blocks faster", category = ModuleCategory.PLAYER)
public class FastMine extends Module {

    private final GroupProperty breakingGroup = new GroupProperty("Breaking", "Block breaking", this);
    private final BooleanProperty enableBreaking = new BooleanProperty("Enable", "Enables faster breaking", true);
    private final EnumProperty<BreakMode> breakingMode = new EnumProperty<>("Mode", "Breaking mode", BreakMode.PROGRESS);
    private final NumberProperty multiplier = new NumberProperty("Multiplier", "Break multiplier", () -> !this.breakingMode.is(BreakMode.INSTANT), 1.25, 1.0, 5.0, 0.05);
    private final NumberProperty hitDelay = new NumberProperty("Hit Delay", "Block hit delay", 1, 0, 5, 1);

    private final EnumProperty<AutoTool> autoTool = new EnumProperty<>("Auto Tool", "elects the best tool for the job!", AutoTool.SILENT);

    private boolean timerSetBack = false;

    public FastMine() {
        this.breakingGroup.addProperties(this.enableBreaking, this.breakingMode, this.multiplier, this.hitDelay);
        this.addProperties(this.breakingGroup, this.autoTool);
    }

    @Override
    public void onEnable() {
        this.timerSetBack = false;
    }

    @Override
    public void onDisable() {
        if (this.timerSetBack) {
            mc.timer.timerSpeed = 1.0F;
            this.timerSetBack = false;
        }
    }

    @EventLink
    public final Listener<EventUpdate> onUpdate = _ -> {
        if (this.enableBreaking.get() && this.breakingMode.is(BreakMode.TIMER)) {
            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && mc.gameSettings.keyBindAttack.isKeyDown()) {
                mc.timer.timerSpeed = this.multiplier.getFloat();
                this.timerSetBack = true;
            } else if (this.timerSetBack) {
                mc.timer.timerSpeed = 1.0F;
                this.timerSetBack = false;
            }
        } else if (this.timerSetBack) {
            mc.timer.timerSpeed = 1.0F;
            this.timerSetBack = false;
        }
    };

    @EventLink
    public final Listener<EventSelectItem> onSelectItem = event -> {

        // Auto tool
        if (this.autoTool.is(AutoTool.NONE) ||  mc.objectMouseOver == null || mc.thePlayer.capabilities.isCreativeMode) {
            return;
        }

        if (mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || !mc.gameSettings.keyBindAttack.isKeyDown()) {
            return;
        }

        Block block = BlockUtil.getBlock(mc.objectMouseOver.getBlockPos());

        float bestSpeed = 1.0F;
        int bestSlot = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);

            if (item == null) {
                continue;
            }

            if (!item.canHarvestBlock(block)) {
                continue;
            }

            float speed = item.getStrVsBlock(block);

            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }

        if (bestSlot != -1) {
            event.slot = bestSlot;
            event.silent = this.autoTool.is(AutoTool.SILENT);
        }

    };

    public float getBreakRequirement() {
        if (!this.isToggled() || !this.enableBreaking.get()) {
            return 1.0F;
        }

        return switch (this.breakingMode.get()) {
            case PROGRESS -> 1.0F / this.multiplier.getFloat();
            case TIMER -> 1.0F;
            case INSTANT -> 0.0F;
        };
    }

    public int getHitDelay() {
        if (!this.isToggled() || !this.enableBreaking.get()) {
            return 5;
        }

        return this.hitDelay.getInt();
    }

    private enum BreakMode {
        PROGRESS,
        TIMER,
        INSTANT,
    }

    private enum AutoTool {
        CLIENT,
        SILENT,
        NONE
    }

}
