package wtf.bhopper.nonsense.network.connection.packet;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import net.minecraft.network.EnumPacketDirection;
import org.apache.logging.log4j.LogManager;
import wtf.bhopper.nonsense.network.connection.packet.client.C00PacketNonsenseLogin;
import wtf.bhopper.nonsense.network.connection.packet.server.S00PacketNonsenseLoginResult;

import java.util.Map;

public enum NonsensePacketRegistry {
    PACKETS {
        {
            this.registerPacket(EnumPacketDirection.CLIENTBOUND, S00PacketNonsenseLoginResult.class);

            this.registerPacket(EnumPacketDirection.SERVERBOUND, C00PacketNonsenseLogin.class);
        }
    };

    private final Map<EnumPacketDirection, BiMap<Integer, Class<? extends NonsensePacket>>> directionMaps;

    NonsensePacketRegistry() {
        this.directionMaps = Maps.newEnumMap(EnumPacketDirection.class);
    }

    protected void registerPacket(EnumPacketDirection direction, Class<? extends NonsensePacket> packetClass) {
        BiMap<Integer, Class<? extends NonsensePacket>> bimap = this.directionMaps.computeIfAbsent(direction, k -> HashBiMap.create());

        if (bimap.containsValue(packetClass)) {
            String s = direction + " packet " + packetClass + " is already known to ID " + bimap.inverse().get(packetClass);
            LogManager.getLogger().fatal(s);
            throw new IllegalArgumentException(s);
        } else {
            bimap.put(bimap.size(), packetClass);
        }
    }

    public Integer getPacketId(EnumPacketDirection direction, NonsensePacket packetIn) {
        return this.directionMaps.get(direction).inverse().get(packetIn.getClass());
    }

    public NonsensePacket getPacket(EnumPacketDirection direction, int packetId) throws InstantiationException, IllegalAccessException {
        Class<? extends NonsensePacket> oclass = this.directionMaps.get(direction).get(packetId);
        return oclass == null ? null : oclass.newInstance();
    }

}
