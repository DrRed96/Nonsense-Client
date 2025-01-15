package wtf.bhopper.nonsense.module.impl.other;

import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventJoinGame;
import wtf.bhopper.nonsense.event.impl.packet.EventReceivePacket;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.module.property.impl.StringProperty;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.misc.Stopwatch;

import java.util.regex.Pattern;

@ModuleInfo(name = "Auto Hypixel",
        description = "Useful mods for Hypixel",
        category = ModuleCategory.OTHER)
public class AutoHypixel extends Module {

    // Taken from Sk1er AutoGG mod XD
    public static final Pattern[] AUTO_GG_REGEX = {
            Pattern.compile("^ +1st Killer - ?\\\\[?\\\\w*\\\\+*\\\\]? \\\\w+ - \\\\d+(?: Kills?)?$"),
            Pattern.compile("^ *1st (?:Place ?)?(?:-|:)? ?\\[?\\w*\\+*\\]? \\w+(?: : \\d+| - \\d+(?: Points?)?| - \\d+(?: x .)?| \\(\\w+ .{1,6}\\) - \\d+ Kills?|: \\d+:\\d+| - \\d+ (?:Zombie )?(?:Kills?|Blocks? Destroyed)| - \\[LINK\\])?$"),
            Pattern.compile("^ +Winn(?:er #1 \\(\\d+ Kills\\): \\w+ \\(\\w+\\)|er(?::| - )(?:Hiders|Seekers|Defenders|Attackers|PLAYERS?|MURDERERS?|Red|Blue|RED|BLU|\\w+)(?: Team)?|ers?: ?\\[?\\w*\\+*\\]? \\w+(?:, ?\\[?\\w*\\+*\\]? \\w+)?|ing Team ?[\\:-] (?:Animals|Hunters|Red|Green|Blue|Yellow|RED|BLU|Survivors|Vampires))$"),
            Pattern.compile("^ +Alpha Infected: \\\\w+ \\\\(\\\\d+ infections?\\\\)$"),
            Pattern.compile("^ +Murderer: \\\\w+ \\\\(\\\\d+ Kills?\\\\)$"),
            Pattern.compile("^ +You survived \\d+ rounds!$"),
            Pattern.compile("^ +(?:UHC|SkyWars|Bridge|Sumo|Classic|OP|MegaWalls|Bow|NoDebuff|Blitz|Combo|Bow Spleef) (?:Duel|Doubles|3v3|4v4|Teams|Deathmatch|2v2v2v2|3v3v3v3)? ?- \\d+:\\d+$"),
            Pattern.compile("^ +They captured all wools!$"),
            Pattern.compile("^ +Game over!$"),
            Pattern.compile("^ +[\\d\\.]+k?/[\\d\\.]+k? \\w+$"),
            Pattern.compile("^ +(?:Criminal|Cop)s won the game!$"),
            Pattern.compile("^ +\\[?\\w*\\+*\\]? \\w+ - \\d+ Final Kills$"),
            Pattern.compile("^ +Zombies - \\d*:?\\d+:\\d+ \\(Round \\d+\\)$"),
            Pattern.compile("^ +. YOUR STATISTICS .$"),
            Pattern.compile("^ {36}Winner(s?)$"),
            Pattern.compile("^ {21}Bridge CTF [a-zA-Z]+ - \\d\\d:\\d\\d$"),
    };

    private final GroupProperty autoGg = new GroupProperty("Auto GG", "Automatically sends 'GG' or another message upon finishing a game", this);
    private final BooleanProperty autoGgEnabled = new BooleanProperty("Enable", "Enables Auto GG", true);
    private final StringProperty autoGgMessage = new StringProperty("Message", "Message to send", "gg");

    private final GroupProperty autoPlay = new GroupProperty("Auto Play", "Automatically plays a new game", this);
    private final BooleanProperty autoPlayEnable = new BooleanProperty("Enable", "Enables auto play.", false);
    private final NumberProperty autoPlayDelay = new NumberProperty("Delay", "Delay before joining a new game.", 3, 0, 5, 1, NumberProperty.FORMAT_SECONDS);

    private final BooleanProperty cleanChat = new BooleanProperty("Clean Chat", "Removes unnecessary messages.", true);
    private final BooleanProperty lobbyJoin = new BooleanProperty("Hide Lobby Join", "Removes lobby join messages.", false);
    private final BooleanProperty autoTip = new BooleanProperty("Auto Tip", "Automatically tips players with active network boosters.", true);

    private final Stopwatch autoGgTimer = new Stopwatch();
    private boolean joined = false;

    public AutoHypixel() {
        super();
        this.autoPlay.addProperties(this.autoPlayEnable, this.autoPlayDelay);
        this.autoGg.addProperties(this.autoGgEnabled, this.autoGgMessage);
        this.addProperties(this.autoGg, this.cleanChat, this.lobbyJoin, this.autoTip);
    }

    @EventLink
    public final Listener<EventTick> onTick = _ -> {
        if (!PlayerUtil.canUpdate()) {
            return;
        }

        if (this.joined) {
            if (this.autoTip.get()) {
                ChatUtil.send("/tipall");
            }
            this.joined = false;
        }

    };

    @EventLink
    public final Listener<EventReceivePacket> onReceivePacket = event -> {
        if (event.packet instanceof S02PacketChat packet) {

            if (!packet.isChat()) {
                return;
            }

            IChatComponent chatComponent = packet.getChatComponent();
            String message = chatComponent.getFormattedText();
            String rawMessage = EnumChatFormatting.getTextWithoutFormattingCodes(message);

            if (this.autoGgEnabled.get()) {
                if (this.autoGgTimer.hasReached(10000)) {
                    for (Pattern pattern : AUTO_GG_REGEX) {
                        if (pattern.matcher(rawMessage).matches()) {
                            ChatUtil.send("%s", this.autoGgMessage.get());
                            this.autoGgTimer.reset();
                            break;
                        }
                    }
                }
            }

            if (this.autoPlayEnable.get()) {
                if (message.contains("play again?")) {
                    for (IChatComponent component : packet.getChatComponent().getSiblings()) {
                        for (String command : component.toString().split("'")) {
                            if (command.startsWith("/play") && !command.contains(".")) {
                                if (this.autoPlayDelay.getInt() == 0) {
                                    ChatUtil.send("%s", command);
                                } else {
                                    new Thread(() -> {
                                        Notification.send("Auto Play", "Joining a new game in " + this.autoPlayDelay.getInt() + " seconds.", NotificationType.INFO, this.autoPlayDelay.getInt() * 1000);
                                        try {
                                            Thread.sleep(this.autoPlayDelay.getInt() * 1000L);
                                        } catch (InterruptedException _) { }
                                        ChatUtil.send("%s", command);
                                    }).start();
                                }
                            }
                        }
                    }
                }
            }

            if (this.cleanChat.get()) {
                if (rawMessage.startsWith("+") && rawMessage.endsWith(" Karma!")) {
                    event.cancel();
                }

                if (rawMessage.equals("Rate this map by clicking: [5] [4] [3] [2] [1]")) {
                    event.cancel();
                }

                if (rawMessage.startsWith("Buy ") && rawMessage.endsWith(" at https://store.hypixel.net")) {
                    event.cancel();
                }

                if (rawMessage.equals("You already tipped everyone that has boosters active, so there isn't anybody to be tipped right now!")) {
                    event.cancel();
                }
            }

            if (this.lobbyJoin.get() && (message.contains("\2476join the lobby!") || message.contains("\2476spooked into the lobby!"))) {
                event.cancel();
            }

        }
    };

    @EventLink
    public final Listener<EventJoinGame> onJoin = eventJoinGame -> this.joined = true;
}
