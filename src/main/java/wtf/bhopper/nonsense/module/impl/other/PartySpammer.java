package wtf.bhopper.nonsense.module.impl.other;

import net.minecraft.network.play.server.S02PacketChat;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventJoinGame;
import wtf.bhopper.nonsense.event.impl.packet.EventReceivePacket;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.module.property.impl.StringProperty;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.misc.Stopwatch;

@ModuleInfo(name = "Party Spammer",
        description = "Spams someone with party invites",
        category = ModuleCategory.OTHER)
public class PartySpammer extends AbstractModule {

    private final StringProperty player = new StringProperty("Player", "Player to spam", "knaall");
    private final NumberProperty delay = new NumberProperty("Delay", "Delay between invites", 250, 1, 1000, 50, NumberProperty.FORMAT_MS);
    private final BooleanProperty cleanChat = new BooleanProperty("Clean Chat", "Hides the party messages", true);

    private final Stopwatch timer = new Stopwatch();
    private boolean inParty = false;

    public PartySpammer() {
        super();
        this.addProperties(this.player, this.delay, this.cleanChat);
    }

    @Override
    public void onEnable() {
        timer.reset();
        ChatUtil.send("/p invite %s", player.get());
    }

    @EventLink
    public final Listener<EventTick> onTick = event -> {

        if (!PlayerUtil.canUpdate()) {
            return;
        }

        if (timer.hasReached(delay.get())) {
            if (this.inParty) {
                ChatUtil.send("/p leave");
            } else {
                ChatUtil.send("/p invite %s", player.get());
            }
            inParty = !inParty;
            timer.reset();
        }
    };

    @EventLink
    public final Listener<EventReceivePacket>  onReceivePacket = event -> {
        if (event.packet instanceof S02PacketChat packet) {

            String text = packet.getChatComponent().getUnformattedText();

            if (text.equals("You cannot invite that player since they have blocked you.")) {
                toggle(false);
                Notification.send("Party Spammer", "Disabled party spammer because the player ignored you... LOL", NotificationType.INFO, 4000);
            }

            if (text.equals("You cannot invite that player.")) {
                toggle(false);
                Notification.send("Party Spammer", "Disabled party spammer because the player has party invites off :(", NotificationType.INFO, 4000);
            }

            if (text.equals("You cannot invite that player since they're not online.")) {
                toggle(false);
                Notification.send("Party Spammer", "Disabled party spammer because the player is offline", NotificationType.INFO, 4000);
            }

            if (text.equals("Couldn't find a player with that name!")) {
                toggle(false);
                Notification.send("Party Spammer", "Disabled party spammer because player does not exist", NotificationType.INFO, 4000);
            }

            if (cleanChat.get()) {
                if (
                        text.equals("-----------------------------------------------------") ||
                                text.endsWith(" to the party! They have 60 seconds to accept.") ||
                                text.equals("You left the party.") ||
                                text.equals("The party was disbanded because all invites expired and the party was empty.") ||
                                text.equals("You are not in a party.") ||
                                text.equals("Woah slow down, you're doing that too fast!") ||
                                text.endsWith(" has already been invited to the party.") ||
                                text.startsWith("You have joined ")
                ) {
                    event.cancel();
                }

                if (text.endsWith(" joined the party.")) {
                    event.cancel();
                    ChatUtil.raw("\2479\247m-----------------------------------------------------");
                    ChatUtil.raw(packet.getChatComponent().getFormattedText());
                    ChatUtil.raw("\2479\247m-----------------------------------------------------");
                }
            }

        }
    };

    @EventLink
    public final Listener<EventJoinGame> onJoin = event -> {
        this.toggle(false);
        Notification.send("Party Spammer", "Party spammer was automatically disabled", NotificationType.INFO, 3000);
    };

}
