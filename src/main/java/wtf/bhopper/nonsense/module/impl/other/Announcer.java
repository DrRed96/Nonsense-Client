package wtf.bhopper.nonsense.module.impl.other;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventReceivePacket;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.util.minecraft.ChatUtil;
import wtf.bhopper.nonsense.util.minecraft.PlayerUtil;
import wtf.bhopper.nonsense.util.misc.CustomCollectors;
import wtf.bhopper.nonsense.util.misc.GeneralUtil;

import java.util.Objects;

@ModuleInfo(name = "Announcer", description = "Announces things in chat.", category = ModuleCategory.OTHER)
public class Announcer extends Module {

    private static final String[] WELCOME_MESSAGES = new String[]{
            "Welcome",
            "Greetings",
            "Hello"
    };

    private final BooleanProperty joins = new BooleanProperty("Joins", "Welcome players that join the server", true);
    private final BooleanProperty items = new BooleanProperty("Items", "Brag about picking up items", true);

    public Announcer() {
        this.autoAddProperties();
    }

    @EventLink
    public final Listener<EventReceivePacket> onReceivePacket = event -> {

        if (!PlayerUtil.canUpdate() || mc.thePlayer.ticksExisted < 20) {
            return;
        }

        if (event.packet instanceof S0DPacketCollectItem packet) {
            if (this.items.get()) {
                Entity playerRaw = mc.theWorld.getEntityByID(packet.getEntityID());
                Entity itemRaw = mc.theWorld.getEntityByID(packet.getCollectedItemEntityID());
                if (playerRaw instanceof EntityPlayer player && itemRaw instanceof EntityItem item) {
                    ItemStack stack = item.getEntityItem();

                    this.sendAction(player, "picked up", I18n.format(stack.getItem().getItemStackDisplayName(stack)));
                }
            }
        }

        if (event.packet instanceof S38PacketPlayerListItem packet) {

            if (this.joins.get()) {
                if (packet.getAction() == S38PacketPlayerListItem.Action.ADD_PLAYER) {
                    ChatUtil.send("%s, %s.", GeneralUtil.randomElement(WELCOME_MESSAGES), String.join(", ", packet.getPlayers()
                            .stream()
                            .collect(CustomCollectors.stringList(data -> data.getProfile().getName()))));
                }
            }
        }
    };

    public void sendAction(Entity entity, String action, String name) {
        String displayName = GeneralUtil.VOWELS.contains(name.substring(0, 1).toLowerCase()) ? "an " + name : "a " + name;
        ChatUtil.send("%s just %s %s!", entity.isClientPlayer() ? "I" : entity.getName(), action, displayName);
    }

}
