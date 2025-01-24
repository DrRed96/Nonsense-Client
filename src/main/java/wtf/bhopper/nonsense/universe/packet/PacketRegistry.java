package wtf.bhopper.nonsense.universe.packet;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import wtf.bhopper.nonsense.universe.packet.api.IPacket;
import wtf.bhopper.nonsense.universe.packet.impl.client.C2SPacketKeepAlive;
import wtf.bhopper.nonsense.universe.packet.impl.client.C2SPacketLogin;
import wtf.bhopper.nonsense.universe.packet.impl.client.C2SPacketMessage;
import wtf.bhopper.nonsense.universe.packet.impl.server.S2CPacketKeepAlive;
import wtf.bhopper.nonsense.universe.packet.impl.server.S2CPacketLoginResponse;
import wtf.bhopper.nonsense.universe.packet.impl.server.S2CPacketMessage;

import java.lang.reflect.InvocationTargetException;

public enum PacketRegistry {

    CLIENT2SERVER {
        {
            this.registerPacket(0x00, C2SPacketLogin.class);
            this.registerPacket(0x01, C2SPacketKeepAlive.class);
            this.registerPacket(0x02, C2SPacketMessage.class);
        }
    },
    SERVER2CLIENT {
        {
            this.registerPacket(0x00, S2CPacketLoginResponse.class);
            this.registerPacket(0x01, S2CPacketKeepAlive.class);
            this.registerPacket(0x02, S2CPacketMessage.class);
        }
    };


    private final BiMap<Integer, Class<? extends IPacket>> packetsMap = HashBiMap.create();

    protected void registerPacket(int id, Class<? extends IPacket> packetClass) {
        if (this.packetsMap.containsValue(packetClass)) {
            throw new IllegalArgumentException(this.name() + " packet " + packetClass + " is already assigned to ID " + this.packetsMap.inverse().get(packetClass));
        }

        this.packetsMap.put(id, packetClass);
    }

    public Integer getPacketId(IPacket packet) {
        return this.packetsMap.inverse().get(packet.getClass());
    }

    public IPacket getPacket(int packetId) throws InstantiationException, IllegalAccessException {
        Class<? extends IPacket> packetClass = this.packetsMap.get(packetId);
        try {
            return packetClass == null ? null : packetClass.getConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException exception) {
            throw new Error(exception);
        }
    }

}
