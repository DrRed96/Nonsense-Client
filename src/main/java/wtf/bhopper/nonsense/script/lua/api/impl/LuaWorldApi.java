package wtf.bhopper.nonsense.script.lua.api.impl;

import net.minecraft.entity.Entity;
import org.luaj.vm2.LuaTable;
import wtf.bhopper.nonsense.script.lua.api.LuaApi;

import java.util.List;

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

        this.addFunc("biome", _ -> valueOf(mc.theWorld.getBiomeGenForCoords(mc.thePlayer.getPosition()).biomeName));
    }

}
