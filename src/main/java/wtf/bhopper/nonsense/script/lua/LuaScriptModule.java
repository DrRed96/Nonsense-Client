package wtf.bhopper.nonsense.script.lua;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.play.client.*;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.packet.EventSendPacket;
import wtf.bhopper.nonsense.event.impl.player.EventPreMotion;
import wtf.bhopper.nonsense.event.impl.player.EventUpdate;
import wtf.bhopper.nonsense.script.ScriptModule;

public class LuaScriptModule extends ScriptModule {

    private final LuaTable moduleTable;

    public LuaScriptModule(String name, String description, LuaTable moduleTable) {
        super(name, description);
        this.moduleTable = moduleTable;

        this.callEvent("on_load");
    }

    @Override
    public void onEnable() {
        this.callEvent("on_enable");
    }

    @Override
    public void onDisable() {
        this.callEvent("on_disable");
    }

    @EventLink
    public final Listener<EventUpdate> onUpdate = _ -> this.callEvent("on_update");

    @EventLink
    public final Listener<EventPreMotion> onPreMotion = event -> {
        LuaTable luaEvent = new LuaTable();
        luaEvent.set("x", event.x);
        luaEvent.set("y", event.y);
        luaEvent.set("z", event.z);
        luaEvent.set("yaw", event.yaw);
        luaEvent.set("pitch", event.pitch);
        luaEvent.set("on_ground", LuaValue.valueOf(event.onGround));

        LuaValue result = this.callEvent("on_pre_motion", luaEvent);
        if (result.istable()) {
            event.x = result.get("x").checkdouble();
            event.y = result.get("y").checkdouble();
            event.z = result.get("z").checkdouble();
            event.yaw = (float)result.get("yaw").checkdouble();
            event.pitch = (float)result.get("pitch").checkdouble();
            event.onGround = result.get("on_ground").checkboolean();
        }
    };

    @EventLink
    public final Listener<EventSendPacket> onSendPacket = event -> {

        LuaTable luaEvent = new LuaTable();
        luaEvent.set("cancel", LuaValue.FALSE);
        luaEvent.set("packet_id", EnumConnectionState.PLAY.getPacketId(EnumPacketDirection.SERVERBOUND, event.packet));

        switch (event.packet) {
            case C00PacketKeepAlive packet -> luaEvent.set("key", packet.getKey());
            case C01PacketChatMessage packet -> luaEvent.set("message", packet.getMessage());
            case C02PacketUseEntity packet -> {
                luaEvent.set("entity", packet.getEntityFromWorld(mc.theWorld).getEntityId());
                luaEvent.set("action", packet.getAction().ordinal());
            }
            case C03PacketPlayer packet -> {
                luaEvent.set("x", packet.getPositionX());
                luaEvent.set("y", packet.getPositionY());
                luaEvent.set("z", packet.getPositionZ());
                luaEvent.set("yaw", packet.getYaw());
                luaEvent.set("pitch", packet.getPitch());
                luaEvent.set("on_ground", LuaValue.valueOf(packet.isOnGround()));
            }
            case C07PacketPlayerDigging packet -> {
                luaEvent.set("status", packet.getStatus().ordinal());
                // TODO: block pos
                luaEvent.set("facing", packet.getFacing().ordinal());
            }
            case C09PacketHeldItemChange packet -> luaEvent.set("slot_id", packet.getSlotId());
            default -> {}
        }

        LuaValue result = this.callEvent("on_send_packet", luaEvent);
        if (result.istable()) {
            if (result.get("cancel").checkboolean()) {
                event.cancel();
            }
        }
    };

    public void callEvent(String name) {
        LuaValue method = this.moduleTable.get(name);
        if (method.isfunction()) {
            Nonsense.getScriptManager().getLuaEnv().runScript(method);
        }
    }

    public LuaValue callEvent(String name, LuaValue event) {
        LuaValue method = this.moduleTable.get(name);
        if (method.isfunction()) {
            return Nonsense.getScriptManager().getLuaEnv().runScript(method, event);
        }

        return LuaValue.NIL;
    }

}
