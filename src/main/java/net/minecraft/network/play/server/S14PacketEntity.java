package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S14PacketEntity implements Packet<INetHandlerPlayClient>
{
    protected int entityId;
    protected byte posX;
    protected byte posY;
    protected byte posZ;
    protected byte yaw;
    protected byte pitch;
    protected boolean onGround;
    protected boolean rotating;

    public S14PacketEntity()
    {
    }

    public S14PacketEntity(int entityIdIn)
    {
        this.entityId = entityIdIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityId = buf.readVarIntFromBuffer();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.entityId);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleEntityMovement(this);
    }

    public String toString()
    {
        return "Entity_" + super.toString();
    }

    public Entity getEntity(World worldIn)
    {
        return worldIn.getEntityByID(this.entityId);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public byte getX()
    {
        return this.posX;
    }

    public byte getY()
    {
        return this.posY;
    }

    public byte getZ()
    {
        return this.posZ;
    }

    public byte getYaw()
    {
        return this.yaw;
    }

    public byte getPitch()
    {
        return this.pitch;
    }

    public boolean isRotating()
    {
        return this.rotating;
    }

    public boolean isOnGround()
    {
        return this.onGround;
    }

    public static class S15PacketEntityRelMove extends S14PacketEntity
    {
        public S15PacketEntityRelMove()
        {
        }

        public S15PacketEntityRelMove(int entityIdIn, byte x, byte y, byte z, boolean onGroundIn)
        {
            super(entityIdIn);
            this.posX = x;
            this.posY = y;
            this.posZ = z;
            this.onGround = onGroundIn;
        }

        public void readPacketData(PacketBuffer buf) throws IOException
        {
            super.readPacketData(buf);
            this.posX = buf.readByte();
            this.posY = buf.readByte();
            this.posZ = buf.readByte();
            this.onGround = buf.readBoolean();
        }

        public void writePacketData(PacketBuffer buf) throws IOException
        {
            super.writePacketData(buf);
            buf.writeByte(this.posX);
            buf.writeByte(this.posY);
            buf.writeByte(this.posZ);
            buf.writeBoolean(this.onGround);
        }
    }

    public static class S16PacketEntityLook extends S14PacketEntity
    {
        public S16PacketEntityLook()
        {
            this.rotating = true;
        }

        public S16PacketEntityLook(int entityIdIn, byte yawIn, byte pitchIn, boolean onGroundIn)
        {
            super(entityIdIn);
            this.yaw = yawIn;
            this.pitch = pitchIn;
            this.rotating = true;
            this.onGround = onGroundIn;
        }

        public void readPacketData(PacketBuffer buf) throws IOException
        {
            super.readPacketData(buf);
            this.yaw = buf.readByte();
            this.pitch = buf.readByte();
            this.onGround = buf.readBoolean();
        }

        public void writePacketData(PacketBuffer buf) throws IOException
        {
            super.writePacketData(buf);
            buf.writeByte(this.yaw);
            buf.writeByte(this.pitch);
            buf.writeBoolean(this.onGround);
        }
    }

    public static class S17PacketEntityLookMove extends S14PacketEntity
    {
        public S17PacketEntityLookMove()
        {
            this.rotating = true;
        }

        public S17PacketEntityLookMove(int entityIdIn, byte x, byte y, byte z, byte yawIn, byte pitchIn, boolean onGroundIn)
        {
            super(entityIdIn);
            this.posX = x;
            this.posY = y;
            this.posZ = z;
            this.yaw = yawIn;
            this.pitch = pitchIn;
            this.onGround = onGroundIn;
            this.rotating = true;
        }

        public void readPacketData(PacketBuffer buf) throws IOException
        {
            super.readPacketData(buf);
            this.posX = buf.readByte();
            this.posY = buf.readByte();
            this.posZ = buf.readByte();
            this.yaw = buf.readByte();
            this.pitch = buf.readByte();
            this.onGround = buf.readBoolean();
        }

        public void writePacketData(PacketBuffer buf) throws IOException
        {
            super.writePacketData(buf);
            buf.writeByte(this.posX);
            buf.writeByte(this.posY);
            buf.writeByte(this.posZ);
            buf.writeByte(this.yaw);
            buf.writeByte(this.pitch);
            buf.writeBoolean(this.onGround);
        }
    }
}
