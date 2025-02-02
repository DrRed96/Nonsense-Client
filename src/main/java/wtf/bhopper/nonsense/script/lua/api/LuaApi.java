package wtf.bhopper.nonsense.script.lua.api;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import wtf.bhopper.nonsense.util.minecraft.IMinecraft;
import wtf.bhopper.nonsense.util.minecraft.inventory.InventoryUtil;
import wtf.bhopper.nonsense.util.minecraft.world.EntityUtil;

import java.util.function.Function;

public abstract class LuaApi extends LuaTable implements IMinecraft {

    public void addFunc(String name, Function<Varargs, Varargs> body) {
        this.set(name, new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                return body.apply(args);
            }
        });
    }

    public static Varargs varargs(LuaValue... values) {
        return LuaValue.varargsOf(values);
    }

    public static <E extends Enum<E>> E getEnum(LuaValue value, Class<E> clazz) {
        return clazz.getEnumConstants()[value.checkint()];
    }

    public static Entity getEntity(LuaValue value) {
        return EntityUtil.getEntity(value.checkint());
    }

    public static BlockPos getBlockPos(LuaValue value) {
        LuaTable table = value.checktable();
        int x = table.get(1).checkint();
        int y = table.get(2).checkint();
        int z = table.get(3).checkint();
        return new BlockPos(x, y, z);
    }

    public static ItemStack getItemStack(LuaValue value) {
        return InventoryUtil.getStack(value.checkint());
    }

}
