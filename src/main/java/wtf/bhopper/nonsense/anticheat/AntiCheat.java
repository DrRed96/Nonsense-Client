package wtf.bhopper.nonsense.anticheat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.reflections.Reflections;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.anticheat.checks.RotationA;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventJoinGame;
import wtf.bhopper.nonsense.event.impl.packet.EventReceivePacket;
import wtf.bhopper.nonsense.event.impl.player.EventUpdate;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.module.impl.combat.AntiBot;
import wtf.bhopper.nonsense.module.impl.other.AntiCheatMod;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.misc.GeneralUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class AntiCheat implements MinecraftInstance {

    private final List<Check> checks = new CopyOnWriteArrayList<>();
    private final Map<UUID, PlayerData> players = new ConcurrentHashMap<>();
    private final List<UUID> flagged = new CopyOnWriteArrayList<>();

    private final AtomicInteger nextChatLine = new AtomicInteger(0x2000);

    public AntiCheat() {
        new Reflections(RotationA.class.getPackage().getName())
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
    public final Listener<EventReceivePacket> onReceivePacket = event -> {

        AntiCheatMod mod = Nonsense.module(AntiCheatMod.class);

        if (!mod.isToggled() || mc.isSingleplayer() || !PlayerUtil.canUpdate()) {
            return;
        }

        for (EntityPlayer player : mc.theWorld.getEntities(EntityPlayer.class, player -> !Nonsense.module(AntiBot.class).isBot(player) && !player.isClientPlayer() && !player.isFake)) {
            PlayerData data = this.players.getOrDefault(player.getUniqueID(), new PlayerData(this.nextChatLine.addAndGet(1)));
            List<String> violated = new ArrayList<>();
            for (Check check : this.checks) {
                if (violated.contains(check.name) || (check.unreliable && !mod.unreliable.get())) {
                    continue;
                }
                if (check.performCheckAndUpdate(player, data, event.packet)) {
                    violated.add(check.name);
                }
            }
            this.players.put(player.getUniqueID(), data);

            if (this.nextChatLine.get() >= 0x3000) {
                this.nextChatLine.set(0x2000);
            }
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
        ChatUtil.Builder.of("%s%s \2477failed check \2476%s \2477\247ox%d", ChatUtil.ANTICHEAT_PREFIX, playerName, check.displayName(), amount)
                .setColor(EnumChatFormatting.GRAY)
                .setHoverEvent(GeneralUtil.paragraph(
                        "\247c\247l" + check.displayName(),
                        "\2477" + check.description + " \2478(VL: " + check.maxViolations + ")"
                )).send(data.chatLine);
    }

    public List<Check> getChecks() {
        return this.checks;
    }

}
