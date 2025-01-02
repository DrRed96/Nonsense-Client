package wtf.bhopper.nonsense.module.impl.combat;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventUpdate;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.util.minecraft.player.PacketUtil;

@ModuleInfo(name = "Fast Bow", description = "Turns your bow into a machine gun.", category = ModuleCategory.COMBAT)
public class FastBow extends Module {

    @EventLink
    public final Listener<EventUpdate> onUpdate = _ -> {
        ItemStack currentItem = mc.thePlayer.inventory.getCurrentItem();
        if (currentItem != null && currentItem.getItem() == Items.bow) {
            if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
                mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, currentItem);
                currentItem.getItem().onItemRightClick(currentItem, mc.theWorld, mc.thePlayer);

                for (int i = 0; i < 20; i++) {
                    PacketUtil.send(new C03PacketPlayer(mc.thePlayer.onGround));
                }

                PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                currentItem.getItem().onPlayerStoppedUsing(currentItem, mc.theWorld, mc.thePlayer, 10);
            }

        }
    };

}
