package wtf.bhopper.nonsense.anticheat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.*;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.anticheat.check.Check;
import wtf.bhopper.nonsense.anticheat.check.impl.AutoBlockA;
import wtf.bhopper.nonsense.anticheat.check.impl.AutoBlockB;
import wtf.bhopper.nonsense.anticheat.check.impl.AutoBlockC;
import wtf.bhopper.nonsense.anticheat.check.impl.RotationA;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.packet.EventReceivePacket;
import wtf.bhopper.nonsense.event.impl.player.EventJoinGame;
import wtf.bhopper.nonsense.module.impl.combat.AntiBot;
import wtf.bhopper.nonsense.module.impl.other.AntiCheatMod;
import wtf.bhopper.nonsense.util.minecraft.IMinecraft;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class AntiCheat implements IMinecraft {

    private final List<Check> checks = new CopyOnWriteArrayList<>();
    private final Map<Integer, PlayerData> playerData = new ConcurrentHashMap<>();

    public AntiCheat() {
        Nonsense.getEventBus().subscribe(this);
        this.addChecks(
                new AutoBlockA(),
                new AutoBlockB(),
                new AutoBlockC(),
                new RotationA()
        );
    }

    private void addChecks(Check... checks) {
        AntiCheatMod mod = Nonsense.module(AntiCheatMod.class);
        for (Check check : checks) {
            this.checks.add(check);
            mod.addCheckProperty(check);
        }
    }

    @EventLink
    public final Listener<EventJoinGame> onJoin = _ -> {
        this.playerData.clear();
    };

    @EventLink
    public final Listener<EventReceivePacket> onReceivePacket = event -> {

        if (!PlayerUtil.canUpdate() || mc.isSingleplayer()) {
            return;
        }

        AntiCheatMod mod = Nonsense.module(AntiCheatMod.class);
        if (!mod.isToggled()) {
            return;
        }

        switch (event.packet) {
            case S14PacketEntity packet -> {
                Entity entity = packet.getEntity(mc.theWorld);
                if (!this.isEntityValid(entity)) {
                    return;
                }

                PlayerData data = this.getPlayerData((EntityPlayer)entity);

                data.handleRelMove(packet);
                for (Check check : this.checks) {
                    if (mod.checkEnabled(check)) {
                        check.handleRelMove(data, packet);
                    }
                }

                this.playerData.put(entity.getEntityId(), data);
            }

            case S18PacketEntityTeleport packet -> {
                Entity entity = mc.theWorld.getEntityByID(packet.getEntityId());
                if (!this.isEntityValid(entity)) {
                    return;
                }

                PlayerData data = this.getPlayerData((EntityPlayer)entity);;

                data.handleTeleport(packet);
                for (Check check : this.checks) {
                    if (mod.checkEnabled(check)) {
                        check.handleTeleport(data, packet);
                    }
                }

                this.playerData.put(entity.getEntityId(), data);
            }

            case S0BPacketAnimation packet -> {
                Entity entity = mc.theWorld.getEntityByID(packet.getEntityID());
                if (!this.isEntityValid(entity)) {
                    return;
                }

                PlayerData data = this.getPlayerData((EntityPlayer)entity);

                data.handleAnimation(packet);
                for (Check check : this.checks) {
                    if (mod.checkEnabled(check)) {
                        check.handleAnimation(data, packet);
                    }
                }

                this.playerData.put(entity.getEntityId(), data);
            }

            case S04PacketEntityEquipment packet -> {
                Entity entity = mc.theWorld.getEntityByID(packet.getEntityID());
                if (!this.isEntityValid(entity)) {
                    return;
                }

                PlayerData data = this.getPlayerData((EntityPlayer)entity);

                data.handleEquipment(packet);
                for (Check check : this.checks) {
                    if (mod.checkEnabled(check)) {
                        check.handleEquipment(data, packet);
                    }
                }

                this.playerData.put(entity.getEntityId(), data);
            }

            case S19PacketEntityHeadLook packet -> {
                Entity entity = packet.getEntity(mc.theWorld);
                if (!this.isEntityValid(entity)) {
                    return;
                }

                PlayerData data = this.getPlayerData((EntityPlayer)entity);

                data.handleHeadLook(packet);
                for (Check check : this.checks) {
                    if (mod.checkEnabled(check)) {
                        check.handleHeadLook(data, packet);
                    }
                }

                this.playerData.put(entity.getEntityId(), data);
            }

            case S1CPacketEntityMetadata packet -> {
                Entity entity = mc.theWorld.getEntityByID(packet.getEntityId());
                if (!this.isEntityValid(entity)) {
                    return;
                }

                PlayerData data = this.getPlayerData((EntityPlayer)entity);

                data.handleEntityMetadata(packet);
                for (Check check : this.checks) {
                    if (mod.checkEnabled(check)) {
                        check.handleEntityMetadata(data, packet);
                    }
                }

                this.playerData.put(entity.getEntityId(), data);
            }

            case S25PacketBlockBreakAnim packet -> {
                Entity entity = mc.theWorld.getEntityByID(packet.getBreakerId());
                if (!this.isEntityValid(entity)) {
                    return;
                }

                PlayerData data = this.getPlayerData((EntityPlayer)entity);

                for (Check check : this.checks) {
                    if (mod.checkEnabled(check)) {
                        check.handleBlockiBreakAnim(data, packet);
                    }
                }

                this.playerData.put(entity.getEntityId(), data);
            }

            default -> {}
        }

    };

    private PlayerData getPlayerData(EntityPlayer entity) {
        return this.playerData.containsKey(entity.getEntityId()) ? this.playerData.get(entity.getEntityId()) : new PlayerData(entity);
    }

    private boolean isEntityValid(Entity entity) {
        return entity instanceof EntityPlayer player && !player.isClientPlayer() && !player.isFake && !Nonsense.module(AntiBot.class).isBot(player);
    }

    public List<Check> getChecks() {
        return this.checks;
    }

}
