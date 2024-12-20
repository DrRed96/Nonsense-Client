package wtf.bhopper.nonsense.module.impl.other;

import com.google.common.base.Objects;
import com.google.common.collect.EvictingQueue;
import io.netty.buffer.Unpooled;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.EnumChatFormatting;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.Event;
import wtf.bhopper.nonsense.event.bus.EventLink;
import wtf.bhopper.nonsense.event.bus.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventPostMotion;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.ButtonProperty;
import wtf.bhopper.nonsense.module.property.impl.EnumProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.network.GuiTestNetwork;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

@ModuleInfo(name = "Debugger", description = "Module to help with debugging.", category = ModuleCategory.OTHER)
public class Debugger extends Module {

    private final GroupProperty loggingGroup = new GroupProperty("Logging", "Logs things in chat", this);
    private final BooleanProperty tick = new BooleanProperty("Tick", "Log the start of a tick", false);
    private final GroupProperty packetDebuggerClient = new GroupProperty("Client Packets", "Client packet debugger", this);
    private final GroupProperty packetDebuggerServer = new GroupProperty("Server Packets", "Server packet debugger", this);
    private final BooleanProperty logPosition = new BooleanProperty("Position", "Logs your position", false);
    private final BooleanProperty hideCancelled = new BooleanProperty("Hide Cancelled", "Hides cancelled packets", false);
    private final ButtonProperty openNetworkTest = new ButtonProperty("Network Tester", "Open the network tester", () -> mc.displayGuiScreen(new GuiTestNetwork()));

    private final Map<Class<? extends Packet>, BooleanProperty> clientPacketSettings = new HashMap<>();
    private final Map<Class<? extends Packet>, BooleanProperty> serverPacketSettings = new HashMap<>();

    private final Queue<PacketInfo> packetCache = EvictingQueue.create(100);

    public Debugger() {

        for (int i = 0; ; i++) {
            try {
                Packet<?> packet = EnumConnectionState.PLAY.getPacket(EnumPacketDirection.SERVERBOUND, i);
                if (packet == null) {
                    break;
                }

                Class<? extends Packet> packetClass = packet.getClass();
                BooleanProperty setting = new BooleanProperty(String.format("C%02X", i), packetClass.getSimpleName(), false);
                this.clientPacketSettings.put(packetClass, setting);
                this.packetDebuggerClient.addProperties(setting);

            } catch (IllegalAccessException | InstantiationException ignored) {}
        }

        for (int i = 0; ; i++) {
            try {
                Packet<?> packet = EnumConnectionState.PLAY.getPacket(EnumPacketDirection.CLIENTBOUND, i);
                if (packet == null) {
                    break;
                }

                Class<? extends Packet> packetClass = packet.getClass();
                BooleanProperty setting = new BooleanProperty(String.format("S%02X", i), packetClass.getSimpleName(), false);
                this.serverPacketSettings.put(packetClass, setting);
                this.packetDebuggerServer.addProperties(setting);

            } catch (IllegalAccessException | InstantiationException ignored) {}
        }

        this.loggingGroup.addProperties(this.tick, this.packetDebuggerClient, this.packetDebuggerServer, this.logPosition, this.hideCancelled);
        this.addProperties(this.loggingGroup, this.openNetworkTest);
    }

    @EventLink
    public final Listener<EventTick> onTick = event -> {
        if (!PlayerUtil.canUpdate()) {
            return;
        }

        if (this.tick.get()) {
            ChatUtil.debug("Tick: %d", mc.thePlayer.ticksExisted);
        }

    };

    @EventLink
    public final Listener<EventPacketDebug> onPacketDebug = event -> {
        try {

            if (event.state == State.CANCELED && this.hideCancelled.get()) {
                return;
            }

            if (event.direction == EventPacketDebug.Direction.OUTGOING) {
                try {
                    if (this.clientPacketSettings.get(event.packet.getClass()).get()) {
                        this.printPacket(event.packet, event.state);
                    }
                } catch (NullPointerException ignored) {}

            } else if (event.direction == EventPacketDebug.Direction.INCOMING) {
                try {
                    if (this.serverPacketSettings.get(event.packet.getClass()).get()) {
                        this.printPacket(event.packet, event.state);
                    }
                } catch (NullPointerException ignored) {}

            }
        } catch (Exception exception) {
            Nonsense.LOGGER.error("Failed to log packet", exception);
        }
    };

    @EventLink
    public final Listener<EventPostMotion> onPost = event -> {
        if (this.logPosition.get()) {
            String client = Objects.toStringHelper("Client")
                    .add("X", mc.thePlayer.posX)
                    .add("Y", mc.thePlayer.posY)
                    .add("Z", mc.thePlayer.posZ)
                    .toString();
            String server = Objects.toStringHelper("Server")
                    .add("X", event.x)
                    .add("Y", event.y)
                    .add("Z", event.z)
                    .toString();
            ChatUtil.debugItem("Position", String.format("%s %s", client, server));
        }

    };

    public void printPacket(Packet packet, State state) {
        String mainValue = getPacketMainValue(packet);

        StringBuilder info = new StringBuilder()
                .append(ChatUtil.CHAT_PREFIX_SHORT)
                .append(state.format)
                .append(packet.getClass().getSimpleName());

        if (mainValue != null) {
            info.append(EnumChatFormatting.GRAY)
                    .append(": ")
                    .append(mainValue);
        }

        PacketInfo cache = new PacketInfo(packet, state);
        this.packetCache.add(cache);

        ChatUtil.Builder.of(info.toString())
                .setHoverEvent("View Packet Info")
                .setClickEvent(ClickEvent.Action.RUN_COMMAND, String.format(".debug packet %d", cache.hashCode()))
                .send();

    }

    public PacketInfo cachedPacket(int hashCode) {
        return packetCache.stream()
                .filter(packet -> packet.hashCode() == hashCode)
                .findFirst()
                .orElse(null);
    }

    public String getPacketMainValue(Packet<?> packet) {
        if (packet instanceof C02PacketUseEntity c02) {
            return String.valueOf(c02.getAction());
        }

        if (packet instanceof C07PacketPlayerDigging c07) {
            return String.valueOf(c07.getStatus());
        }

        if (packet instanceof C08PacketPlayerBlockPlacement c08) {
            return String.valueOf(c08.getStack());
        }

        if (packet instanceof C09PacketHeldItemChange c09) {
            return Integer.toString(c09.getSlotId());
        }

        if (packet instanceof C0BPacketEntityAction c0b) {
            return String.valueOf(c0b.getAction());
        }

        if (packet instanceof C0EPacketClickWindow c0e) {
            return Integer.toString(c0e.getSlotId());
        }

        if (packet instanceof C0FPacketConfirmTransaction c0f) {
            return Short.toString(c0f.getUid());
        }

        if (packet instanceof C10PacketCreativeInventoryAction c10) {
            return String.valueOf(c10.getStack());
        }

        if (packet instanceof C12PacketUpdateSign c12) {
            return String.valueOf(c12.getPosition());
        }

        if (packet instanceof C17PacketCustomPayload c17) {
            return c17.getChannelName();
        }

        if (packet instanceof C18PacketSpectate c18) {
            return String.valueOf(c18.getId());
        }

        if (packet instanceof C19PacketResourcePackStatus c19) {
            return String.valueOf(c19.getStatus());
        }

        if (packet instanceof S2FPacketSetSlot s2f) {
            return Integer.toString(s2f.getSlot());
        }

        return null;
    }

    public enum State {
        NORMAL(EnumChatFormatting.GREEN),
        NO_EVENT(EnumChatFormatting.YELLOW),
        CANCELED(EnumChatFormatting.RED);

        public final EnumChatFormatting format;

        State(EnumChatFormatting format) {
            this.format = format;
        }

        public String getString() {
            return String.format("%s%s", format, EnumProperty.toDisplay(this));
        }
    }

    public static class PacketInfo {

        public final String clazz;
        public final State state;
        public final FieldInfo[] fields;
        public final int size;

        public PacketInfo(Packet packet, State state) {
            this.clazz = packet.getClass().getSimpleName();
            this.state = state;
            Field[] declaredFields = packet.getClass().getDeclaredFields();
            this.fields = new FieldInfo[declaredFields.length];
            for (int i = 0; i < declaredFields.length; i++) {
                Field field = declaredFields[i];
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    this.fields[i] = new FieldInfo(field.getType().getSimpleName(), field.getName(), String.valueOf(field.get(packet)));
                } catch (IllegalAccessException | IllegalArgumentException exception) {
                    this.fields[i] = new FieldInfo(field.getType().getSimpleName(), field.getName(), "\247cError");
                }
            }

            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            int s = -1;
            try {
                packet.writePacketData(buffer);
                s = buffer.writerIndex();
            } catch (IOException ignored) { }
            this.size = s;
            buffer.clear();
        }

        public void print() {
            ChatUtil.debugTitle("Packet Info");
            ChatUtil.debugItem("Class", this.clazz);
            ChatUtil.debugItem("State", state.getString());

            if (this.fields.length > 0) {
                String[] printFields = new String[this.fields.length];
                for (int i = 0; i < this.fields.length; i++) {
                    if (this.fields[i] == null) {
                        continue;
                    }
                    printFields[i] = String.format("  \2478[\2473%s\2478] \247b%s\2478: \2477%s", this.fields[i].type, this.fields[i].name, this.fields[i].value);
                }
                ChatUtil.debugList("Fields", printFields);
            }
//            ChatUtil.debugItem("Size", this.size == -1 ? "Error" : String.format("%d / 0x%X", this.size, this.size));
        }

        public static class FieldInfo {
            String type;
            String name;
            String value;

            public FieldInfo(String type, String name, String value) {
                this.type = type;
                this.name = name;
                this.value = value;
            }
        }
    }

    public record EventPacketDebug(Packet packet,
                                   State state,
                                   Debugger.EventPacketDebug.Direction direction) implements Event {

        public enum Direction {
            INCOMING,
            OUTGOING
        }
    }

}
