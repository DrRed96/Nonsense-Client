package wtf.bhopper.nonsense.script.lua.api.impl;

import net.minecraft.client.Minecraft;
import wtf.bhopper.nonsense.component.impl.packet.PingComponent;
import wtf.bhopper.nonsense.script.lua.api.LuaApi;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;

public class LuaClientApi extends LuaApi {

    public LuaClientApi() {

        this.addFunc("time", _ -> valueOf(System.currentTimeMillis()));

        this.addFunc("print", args -> {
            String message = args.arg(1).checkjstring();
            ChatUtil.print("%s", message);
            return NIL;
        });

        this.addFunc("fps", _ -> valueOf(Minecraft.getDebugFPS()));

        this.addFunc("ping", _ -> valueOf(PingComponent.getPing()));

    }

}
