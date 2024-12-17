package wtf.bhopper.nonsense.script.api.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;

public class ClientLuaApi extends LuaTable {

    public ClientLuaApi() {
        this.set("print", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue message) {
                ChatUtil.print("%s", message.checkjstring());
                return NIL;
            }
        });
    }

}
