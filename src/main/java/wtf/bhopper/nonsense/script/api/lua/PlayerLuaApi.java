package wtf.bhopper.nonsense.script.api.lua;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import wtf.bhopper.nonsense.script.api.lua.types.LuaTypes;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;
import wtf.bhopper.nonsense.util.minecraft.PacketUtil;

public class PlayerLuaApi extends LuaTable implements MinecraftInstance {

    public PlayerLuaApi() {
        this.set("jump", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                mc.thePlayer.jump();
                return NIL;
            }
        });
        this.set("send_packet", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue id, LuaValue info) {
                PacketUtil.send(getPacket(id.checkint(), info.checktable()));
                return NIL;
            }
        });
        this.set("send_packet_no_event", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue id, LuaValue info) {
                PacketUtil.sendNoEvent(getPacket(id.checkint(), info.checktable()));
                return NIL;
            }
        });
        this.set("window_click", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs varargs) {
                return super.invoke(varargs);
            }
        });
    }

    private static Packet<?> getPacket(int id, LuaTable info) {
        return switch (id) {
            case 0x01 -> new C01PacketChatMessage(info.get("message").checkjstring());
            case 0x02 -> {
                C02PacketUseEntity.Action action = LuaTypes.getEnum(info.get("action"), C02PacketUseEntity.Action.class);
                if (action == C02PacketUseEntity.Action.INTERACT_AT) {
                    yield new C02PacketUseEntity(mc.theWorld.getEntityByID(info.get("entity").checkint()), LuaTypes.getVec3(info.get("hit_vec")));
                }
                yield new C02PacketUseEntity(mc.theWorld.getEntityByID(info.get("entity").checkint()), action);
            }
            case 0x03 -> new C03PacketPlayer(info.get("on_ground").checkboolean());
            case 0x04 -> new C03PacketPlayer.C04PacketPlayerPosition(info.get("x").checkdouble(), info.get("y").checkdouble(), info.get("z").checkdouble(), info.get("on_ground").checkboolean());
            case 0x07 -> new C07PacketPlayerDigging(LuaTypes.getEnum(info.get("action"), C07PacketPlayerDigging.Action.class), LuaTypes.getBlockPos(info.get("pos")), LuaTypes.getEnum(info.get("facing"), EnumFacing.class));

            default -> throw new IllegalArgumentException(String.format("Invalid packet ID: 0x%02X", id));
        };
    }


}
