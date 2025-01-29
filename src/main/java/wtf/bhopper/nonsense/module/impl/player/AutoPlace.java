package wtf.bhopper.nonsense.module.impl.player;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventUpdate;
import wtf.bhopper.nonsense.event.impl.player.interact.EventClickAction;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.impl.movement.Scaffold;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;

@ModuleInfo(name = "Auto Place",
        description = "Automatically places blocks",
        category = ModuleCategory.PLAYER,
        searchAlias = "Legit Scaffold")
public class AutoPlace extends AbstractModule {

    private final NumberProperty delay = new NumberProperty("Delay", "Block place delay in ticks", 0, 0, 3, 1);
    private final BooleanProperty click = new BooleanProperty("Right Click", "Requires you to hold down right click.", true);

    private int ticks = 0;

    public AutoPlace() {
        this.addProperties(this.delay, this.click);
    }

    @EventLink
    public final Listener<EventUpdate> onUpdate = _ -> this.ticks--;

    @EventLink
    public final Listener<EventClickAction> onClick = event -> {
        if (this.canPlace(event)) {
            event.right = true;
            this.ticks = this.delay.getInt();
        }
    };

    private boolean canPlace(EventClickAction event) {
        if (event.mouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return false;
        }

        if (this.click.get() && !mc.gameSettings.keyBindUseItem.isKeyDown()) {
            return false;
        }

        if (this.ticks > 0) {
            return false;
        }

        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if (heldItem == null) {
            return false;
        }

        if (heldItem.getItem() instanceof ItemBlock block) {
            return !Scaffold.BAD_BLOCKS.contains(block.getBlock());
        }

        return false;
    }

}
