package wtf.bhopper.nonsense.script.lua.api.impl;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.luaj.vm2.Varargs;
import wtf.bhopper.nonsense.script.lua.api.LuaApi;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PacketUtil;

public class LuaPlayerApi extends LuaApi {

    public LuaPlayerApi() {

        this.addFunc("jump", _ -> {
            mc.thePlayer.jump();
            return NIL;
        });

        this.addFunc("message", args -> {
            ChatUtil.send("%s", args.arg(1).checkjstring());
            return NIL;
        });

        this.addFunc("strafe", _ -> varargs(
                valueOf(mc.thePlayer.moveForward),
                valueOf(mc.thePlayer.moveStrafing)
        ));

        this.addFunc("position", _ -> varargs(
                valueOf(mc.thePlayer.posX),
                valueOf(mc.thePlayer.posY),
                valueOf(mc.thePlayer.posZ)
        ));

        this.addFunc("prev_position", _ -> varargs(
                valueOf(mc.thePlayer.prevPosX),
                valueOf(mc.thePlayer.prevPosY),
                valueOf(mc.thePlayer.prevPosZ)
        ));

        this.addFunc("motion", _ -> varargs(
                valueOf(mc.thePlayer.motionX),
                valueOf(mc.thePlayer.motionY),
                valueOf(mc.thePlayer.motionZ)
        ));

        this.addFunc("set_motion", args -> {
            double x = args.arg(1).checkdouble();
            double y = args.arg(2).checkdouble();
            double z = args.arg(3).checkdouble();
            mc.thePlayer.setVelocity(x, y, z);
            return NIL;
        });

        this.addFunc("set_position", args -> {
            double x = args.arg(1).checkdouble();
            double y = args.arg(2).checkdouble();
            double z = args.arg(3).checkdouble();
            mc.thePlayer.setPosition(x, y, z);
            return NIL;
        });

        this.addFunc("set_sprinting", args -> {
            mc.thePlayer.setSprinting(args.arg(1).checkboolean());
            return NIL;
        });

        this.addFunc("distance_to_entity", args -> {
            int entityId = args.arg(1).checkint();
            Entity entity = mc.theWorld.getEntityByID(entityId);
            if (entity == null) {
                return NIL;
            }
            return valueOf(mc.thePlayer.getDistanceToEntity(entity));
        });

        this.addFunc("distance_to", args -> {
            double x = args.arg(1).checkdouble();
            double y = args.arg(2).checkdouble();
            double z = args.arg(3).checkdouble();
            return valueOf(mc.thePlayer.getDistance(x, y, z));
        });

        this.addFunc("send_packet", args -> {
            Packet<?> packet = this.getPacketFromArgs(args);
            if (packet != null) {
                PacketUtil.send(packet);
            }
            return NIL;
        });

        this.addFunc("send_packet_no_event", args -> {
            Packet<?> packet = this.getPacketFromArgs(args);
            if (packet != null) {
                PacketUtil.sendNoEvent(packet);
            }
            return NIL;
        });
        
        this.addFunc("angles", _ -> varargs(valueOf(mc.thePlayer.rotationYaw), valueOf(mc.thePlayer.rotationPitch)));

        this.addFunc("prev_angles", _ -> varargs(valueOf(mc.thePlayer.prevRotationYaw), valueOf(mc.thePlayer.prevRotationPitch)));
        
        this.addFunc("set_angles", args -> {
            mc.thePlayer.rotationYaw = (float)args.arg(2).checkdouble();
            mc.thePlayer.rotationPitch = (float)args.arg(3).checkdouble();
            return NIL;
        });
        
        this.addFunc("using_item", _ -> valueOf(mc.thePlayer.isUsingItem()));

        this.addFunc("swing_item", _ -> {
            mc.thePlayer.swingItem();
            return NIL;
        });

        this.addFunc("use_item", _ -> {
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
            return NIL;
        });

        this.addFunc("id", _ -> valueOf(mc.thePlayer.getEntityId()));

        this.addFunc("health", _ -> valueOf(mc.thePlayer.getHealth()));

        this.addFunc("fall_distance", _ -> valueOf(mc.thePlayer.fallDistance));

        this.addFunc("max_health", _ -> valueOf(mc.thePlayer.getMaxHealth()));

        this.addFunc("held_item", _ -> {
            ItemStack heldItem = mc.thePlayer.getHeldItem();
            if (heldItem == null) {
                return NIL;
            }
            return valueOf(heldItem.getItem().getUnlocalizedName(heldItem));
        });

        this.addFunc("name", _ -> valueOf(mc.thePlayer.getName()));

        this.addFunc("base_speed", _ -> valueOf(MoveUtil.baseSpeedStrafe()));

        this.addFunc("held_item_slot", _ -> valueOf(mc.thePlayer.inventory.currentItem));

        this.addFunc("set_held_item_slot", args -> {
            mc.thePlayer.inventory.currentItem = args.arg(2).checkint();
            return NIL;
        });

        this.addFunc("hurt_time", _ -> valueOf(mc.thePlayer.hurtTime));
        
    }

    private Packet<?> getPacketFromArgs(Varargs args) {
        int packetId = args.arg(1).checkint();

        return switch (packetId) {
            case 0x00 -> {
                int key = args.arg(2).checkint();
                yield new C00PacketKeepAlive(key);
            }
            case 0x01 -> {
                String message = args.arg(2).checkjstring();
                yield new C01PacketChatMessage(message);
            }
            case 0x02 -> {
                Entity entity = getEntity(args.arg(2));
                C02PacketUseEntity.Action action = getEnum(args.arg(3), C02PacketUseEntity.Action.class);
                yield new C02PacketUseEntity(entity, action);
            }
            case 0x03 -> {
                boolean onGround = args.arg(2).checkboolean();
                yield new C03PacketPlayer(onGround);
            }
            case 0x04 -> {
                double x = args.arg(2).checkdouble();
                double y = args.arg(3).checkdouble();
                double z = args.arg(4).checkdouble();
                boolean onGround = args.arg(5).checkboolean();
                yield new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, onGround);
            }
            case 0x05 -> {
                float yaw = (float)args.arg(2).checkdouble();
                float pitch = (float)args.arg(3).checkdouble();
                boolean onGround = args.arg(4).checkboolean();
                yield new C03PacketPlayer.C05PacketPlayerLook(yaw, pitch, onGround);
            }
            case 0x06 -> {
                double x = args.arg(2).checkdouble();
                double y = args.arg(3).checkdouble();
                double z = args.arg(4).checkdouble();
                float yaw = (float)args.arg(5).checkdouble();
                float pitch = (float)args.arg(6).checkdouble();
                boolean onGround = args.arg(7).checkboolean();
                yield new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, yaw, pitch, onGround);
            }
            case 0x07 -> {
                C07PacketPlayerDigging.Action action = getEnum(args.arg(2), C07PacketPlayerDigging.Action.class);
                BlockPos pos = getBlockPos(args.arg(3));
                EnumFacing facing = EnumFacing.values()[args.arg(4).checkint()];
                yield new C07PacketPlayerDigging(action, pos, facing);
            }
            case 0x08 -> {
                if (args.arg(2).isnil()) {
                    yield new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem());
                }
                if (args.arg(2).isinttype()) {
                    ItemStack itemStack = getItemStack(args.arg(2));
                    yield new C08PacketPlayerBlockPlacement(itemStack);
                }

                BlockPos pos = getBlockPos(args.arg(2));
                int placedBlockDirection = args.arg(3).checkint();
                ItemStack itemStack = getItemStack(args.arg(4));
                float facingX = (float)args.arg(5).checkdouble();
                float facingY = (float)args.arg(6).checkdouble();
                float facingZ = (float)args.arg(7).checkdouble();
                yield new C08PacketPlayerBlockPlacement(pos, placedBlockDirection, itemStack, facingX, facingY, facingZ);
            }
            case 0x09 -> {
                int slot = args.arg(2).checkint();
                yield new C09PacketHeldItemChange(slot);
            }
            case 0x0A -> new C0APacketAnimation();
            case 0x0B -> {
                Entity entity = getEntity(args.arg(2));
                C0BPacketEntityAction.Action action = getEnum(args.arg(3), C0BPacketEntityAction.Action.class);
                int auxData = 0;
                if (args.arg(4).isinttype()) {
                    auxData = args.arg(4).checkint();
                }
                yield new C0BPacketEntityAction(entity, action, auxData);
            }
            case 0x0F -> {
                int windowId = args.arg(2).checkint();
                short uid = (short)args.arg(3).checkint();
                boolean accepted = args.arg(4).checkboolean();
                yield new C0FPacketConfirmTransaction(windowId, uid, accepted);
            }
            case 0x17 -> {
                String channel = args.arg(2).checkjstring();
                PacketBuffer data = PacketUtil.createByteBuffer(args.arg(3).checkjstring());
                yield new C17PacketCustomPayload(channel, data);
            }
            default -> null;
        };
    }


}
