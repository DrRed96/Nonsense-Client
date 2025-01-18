package wtf.bhopper.nonsense.script.lua.api.impl;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.impl.combat.AntiBot;
import wtf.bhopper.nonsense.script.lua.api.LuaApi;
import wtf.bhopper.nonsense.util.minecraft.world.BlockUtil;

public class LuaWorldApi extends LuaApi {

    public LuaWorldApi() {

        this.addFunc("timer", _ -> valueOf(mc.timer.timerSpeed));

        this.addFunc("set_timer", args -> {
            mc.timer.timerSpeed = (float)args.arg(1).checkdouble();
            return NIL;
        });

        this.addFunc("refresh_chunks", _ -> {
            mc.renderGlobal.loadRenderers();
            return NIL;
        });

        this.addFunc("entities", _ -> {
            LuaTable table = new LuaTable();

            mc.theWorld.loadedEntityList.stream()
                    .map(Entity::getEntityId)
                    .forEach(table::add);

            return table;
        });

        this.addFunc("block", args -> {
            int x = args.arg(1).checkint();
            int y = args.arg(2).checkint();
            int z = args.arg(3).checkint();
            BlockPos pos = new BlockPos(x, y, z);
            Block block = BlockUtil.getBlock(pos);
            return valueOf(block.getUnlocalizedName());
        });

        this.addFunc("name", args -> {
            Entity entity = getEntity(args.arg(1));
            return valueOf(entity.getName());
        });

        this.addFunc("display_name", args -> {
            Entity entity = getEntity(args.arg(1));
            return valueOf(entity.getDisplayName().getFormattedText());
        });

        this.addFunc("held_item", args -> {
            Entity entity = getEntity(args.arg(1));
            if (entity instanceof EntityLivingBase living) {
                ItemStack heldItem = living.getHeldItem();
                if (heldItem != null) {
                    return valueOf(heldItem.getUnlocalizedName());
                }
            }
            return NIL;
        });

        this.addFunc("hurt_time", args -> {
            Entity entity = getEntity(args.arg(1));
            if (entity instanceof EntityLivingBase living) {
                return valueOf(living.hurtTime);
            }
            return NIL;
        });

        this.addFunc("health", args -> {
            Entity entity = getEntity(args.arg(1));
            if (entity instanceof EntityLivingBase living) {
                return valueOf(living.getHealth());
            }
            return NIL;
        });

        this.addFunc("max_health", args -> {
            Entity entity = getEntity(args.arg(1));
            if (entity instanceof EntityLivingBase living) {
                return valueOf(living.getMaxHealth());
            }
            return NIL;
        });

        this.addFunc("sprinting", args -> {
            Entity entity = getEntity(args.arg(1));
            return valueOf(entity.isSprinting());
        });

        this.addFunc("facing", args -> {
            Entity entity = getEntity(args.arg(1));
            return valueOf(entity.getHorizontalFacing().ordinal());
        });

        this.addFunc("angles", args -> {
            Entity entity = getEntity(args.arg(1));
            return varargs(valueOf(entity.rotationYaw), valueOf(entity.rotationPitch));
        });

        this.addFunc("bounding_box", args -> {
            Entity entity = getEntity(args.arg(1));
            AxisAlignedBB box = entity.getEntityBoundingBox();
            return varargs(
                    valueOf(box.minX),
                    valueOf(box.minY),
                    valueOf(box.minZ),
                    valueOf(box.maxX),
                    valueOf(box.maxY),
                    valueOf(box.maxZ)
            );
        });

        this.addFunc("ticks_existed", args -> {
            Entity entity = getEntity(args.arg(1));
            return valueOf(entity.ticksExisted);
        });

        this.addFunc("is_player", args -> {
            Entity entity = getEntity(args.arg(1));
            return valueOf(entity instanceof EntityPlayer);
        });

        this.addFunc("width_height", args -> {
            Entity entity = getEntity(args.arg(1));
            return varargs(valueOf(entity.width), valueOf(entity.height));
        });

        this.addFunc("is_sneaking", args -> {
            Entity entity = getEntity(args.arg(1));
            return valueOf(entity.isSneaking());
        });

        this.addFunc("is_invisible", args -> {
            Entity entity = getEntity(args.arg(1));
            return valueOf(entity.isInvisible());
        });

        this.addFunc("burning", args -> {
            Entity entity = getEntity(args.arg(1));
            return valueOf(entity.isBurning());
        });

        this.addFunc("is_bot", args -> {
            Entity entity = getEntity(args.arg(1));
            return valueOf(Nonsense.module(AntiBot.class).isBot(entity));
        });

        this.addFunc("riding", args -> {
            Entity entity = getEntity(args.arg(1));
            return valueOf(entity.isRiding());
        });

        this.addFunc("position", args -> {
            Entity entity = getEntity(args.arg(1));
            return varargs(valueOf(entity.posX), valueOf(entity.posY), valueOf(entity.posZ));
        });

        this.addFunc("prev_position", args -> {
            Entity entity = getEntity(args.arg(1));
            return varargs(valueOf(entity.prevPosX), valueOf(entity.prevPosY), valueOf(entity.prevPosZ));
        });

        this.addFunc("is_potion_active", args -> {
            Entity entity = getEntity(args.arg(1));
            int potionId = args.arg(2).checkint();
            if (entity instanceof EntityLivingBase living) {
                return valueOf(living.isPotionActive(potionId));
            }

            return LuaValue.FALSE;
        });

        this.addFunc("remove", args -> {
            Entity entity = getEntity(args.arg(1));
            mc.theWorld.removeEntity(entity);
            return NIL;
        });

        this.addFunc("biome", _ -> valueOf(mc.theWorld.getBiomeGenForCoords(mc.thePlayer.getPosition()).biomeName));
    }

}
