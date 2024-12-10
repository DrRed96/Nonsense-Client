package wtf.bhopper.nonsense.anticheat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.reflections.Reflections;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.anticheat.checks.NoSlowA;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.EventJoinGame;
import wtf.bhopper.nonsense.event.impl.EventReceivePacket;
import wtf.bhopper.nonsense.event.impl.EventUpdate;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.module.impl.combat.AntiBot;
import wtf.bhopper.nonsense.module.impl.other.AntiCheatMod;
import wtf.bhopper.nonsense.util.minecraft.ChatUtil;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;
import wtf.bhopper.nonsense.util.misc.GeneralUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AntiCheat implements MinecraftInstance {

    private final List<Check> checks = new ArrayList<>();
    private final Map<UUID, PlayerData> players = new ConcurrentHashMap<>();
    private final List<UUID> flagged = new ArrayList<>();

    private int nextChatLine = 0x7FFF;

    public AntiCheat() {
        new Reflections(NoSlowA.class.getPackage().getName())
                .getSubTypesOf(Check.class)
                .stream()
                .sorted(Comparator.comparing(check -> check.getAnnotation(CheckInfo.class).name()))
                .forEach(check -> {
                    try {
                        this.checks.add(check.getConstructor().newInstance());
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException(e);
                    }
                });

        Nonsense.getEventBus().subscribe(this);
    }

    private void reset() {
        for (Check check : this.checks) {
            check.reset();
        }
        this.players.clear();
        this.flagged.clear();
    }

    @EventLink
    public final Listener<EventJoinGame> onJoin = _ -> this.reset();

    @EventLink
    public final Listener<EventUpdate> onUpdate = _ -> {

        AntiCheatMod mod = Nonsense.module(AntiCheatMod.class);

        if (!mod.isToggled()) {
            return;
        }

        for (EntityPlayer player : mc.theWorld.getEntities(EntityPlayer.class, player -> !Nonsense.module(AntiBot.class).isBot(player) && !player.isClientPlayer())) {
            PlayerData data = this.players.getOrDefault(player.getUniqueID(), new PlayerData(++this.nextChatLine));
            data.update(player);
            List<String> violated = new ArrayList<>();
            for (Check check : this.checks) {
                if (violated.contains(check.name) || (check.unreliable && !mod.unreliable.get())) {
                    continue;
                }
                if (check.check(player, data)) {
                    violated.add(check.name);
                }
            }
            this.players.put(player.getUniqueID(), data);
        }
    };

    @EventLink
    public final Listener<EventReceivePacket> onReceivePacket = event -> {
        for (PlayerData data : this.players.values()) {
            data.onPacket(event.packet);
        }
    };

    public void flag(EntityPlayer player) {
        if (this.flagged.contains(player.getUniqueID())) {
            return;
        }
        this.flagged.add(player.getUniqueID());
        Notification.send("Anti Cheat", player.getName() + " is cheating", NotificationType.WARNING, 5000);
    }

    public void notifyViolation(EntityPlayer player, PlayerData data, Check check, int amount) {
        String playerName = this.flagged.contains(player.getUniqueID()) ? "\247c" + player.getName() : player.getName();
        IChatComponent component = ChatUtil.Builder.of("%s%s \2477failed check \2476%s \2477\247ox%d", ChatUtil.ANTICHEAT_PREFIX, playerName, check.displayName(), amount)
                .setColor(EnumChatFormatting.GRAY)
                .setHoverEvent(GeneralUtil.paragraph(
                        "\247c\247l" + check.displayName(),
                        "\2477" + check.description + " \2478(" + check.maxViolations + " violations to flag)"
                )).build();

        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(component, data.chatLine);
    }

    public List<Check> getChecks() {
        return this.checks;
    }

}
