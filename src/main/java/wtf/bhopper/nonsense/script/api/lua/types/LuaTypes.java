package wtf.bhopper.nonsense.script.api.lua.types;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.luaj.vm2.LuaValue;

public class LuaTypes {

    public static <T extends Enum<T>> T getEnum(LuaValue value, Class<T> type) {
        return type.getEnumConstants()[value.checkint()];
    }

    public static BlockPos getBlockPos(LuaValue value) {
        return new BlockPos(value.get("x").checkint(), value.get("y").checkint(), value.get("z").checkint());
    }

    public static Vec3 getVec3(LuaValue value) {
        return new Vec3(value.get("x").checkdouble(), value.get("y").checkdouble(), value.get("z").checkdouble());
    }

}
