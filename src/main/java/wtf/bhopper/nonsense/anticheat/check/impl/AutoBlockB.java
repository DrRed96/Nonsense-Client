package wtf.bhopper.nonsense.anticheat.check.impl;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import wtf.bhopper.nonsense.anticheat.PlayerData;
import wtf.bhopper.nonsense.anticheat.check.AbstractCheck;
import wtf.bhopper.nonsense.anticheat.check.CheckInfo;

@CheckInfo(name = "Auto-Block B",
        description = "Impossible 'Sprinting' and 'Use-Item' status combination.",
        maxVl = 4)
public class AutoBlockB extends AbstractCheck {

    @Override
    public void handleEntityMetadata(PlayerData data, S1CPacketEntityMetadata packet) {
        if (packet.getMetadata() == null) {
            return;
        }

        if (data.getBlocks() > 1 && data.isUsingItem() && data.isSprinting()) {
            ItemStack stack = data.getEntity().getHeldItem();
            if (stack != null && stack.getItem() instanceof ItemSword) {
                data.incrementVl(this);
            }
        }

    }

}
