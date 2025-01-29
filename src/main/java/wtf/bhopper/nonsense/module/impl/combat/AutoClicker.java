package wtf.bhopper.nonsense.module.impl.combat;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.MovingObjectPosition;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.EventPriorities;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.interact.EventClickAction;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.util.misc.MathUtil;
import wtf.bhopper.nonsense.util.misc.Stopwatch;

@ModuleInfo(name = "Auto Clicker",
        description = "Automatically clicks",
        category = ModuleCategory.COMBAT)
public class AutoClicker extends AbstractModule {

    private final NumberProperty minCps = new NumberProperty("Min CPS", "Minimum clicks per second", 12.0, 1.0, 20.0, 0.01);
    private final NumberProperty maxCps = new NumberProperty("Min CPS", "Minimum clicks per second", 14.0, 1.0, 20.0, 0.01);

    private final BooleanProperty weapon = new BooleanProperty("Weapon Check", "Disable auto clicker when not holding a weapon.", false);
    private final BooleanProperty mining = new BooleanProperty("Mining Check", "Disable auto clicker when mining a block.", true);
    private final BooleanProperty trigger = new BooleanProperty("Trigger Mode", "Only auto click while looking at an entity.", false);

    private int nextDelay = 0;
    private final Stopwatch stopwatch = new Stopwatch();

    public AutoClicker() {
        this.addProperties(this.minCps, this.maxCps, this.weapon, this.mining, this.trigger);

        this.minCps.addValueChangeListener((_, value) -> {
            if (value > this.maxCps.getDouble()) {
                this.maxCps.set(value);
            }
        });

        this.maxCps.addValueChangeListener((_, value) -> {
            if (value < this.minCps.getDouble()) {
                this.minCps.set(value);
            }
        });
    }

    @EventLink(EventPriorities.VERY_HIGH)
    public final Listener<EventClickAction> onClick = event -> {
        if (!event.usingItem && this.canClick()) {
            event.left = true;
            this.nextDelay = (int)(1000.0 / MathUtil.random(minCps.getDouble(), maxCps.getDouble()));
        }
    };

    private boolean canClick() {

        if (!mc.gameSettings.keyBindAttack.isKeyDown() || !this.stopwatch.hasReached(this.nextDelay)) {
            return false;
        }

        if (this.weapon.get()) {
            ItemStack heldItem = mc.thePlayer.getHeldItem();
            if (heldItem == null || !(heldItem.getItem() instanceof ItemSword || heldItem.getItem() instanceof ItemTool)) {
                return false;
            }
        }

        if (this.mining.get()) {
            if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                return false;
            }
        }

        if (this.trigger.get()) {
            if (mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
                return false;
            }
        }

        return true;
    }

}
