package wtf.bhopper.nonsense.script.lua.api.impl;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import wtf.bhopper.nonsense.script.lua.api.LuaApi;

public class LuaInputApi extends LuaApi {

    public LuaInputApi() {
        this.addFunc("is_key_down", args -> {
            int keyCode = args.arg(1).checkint();
            return valueOf(Keyboard.isKeyDown(keyCode));
        });

        this.addFunc("is_mouse_down", args -> {
            int mouseButton = args.arg(1).checkint();
            return valueOf(Mouse.isButtonDown(mouseButton));
        });

        this.addFunc("get_key_name", args -> {
            int keyCode = args.arg(1).checkint();
            return valueOf(Keyboard.getKeyName(keyCode));
        });

        this.addFunc("get_key_number", args -> {
            String name = args.arg(1).checkjstring();
            return valueOf(Keyboard.getKeyIndex(name));
        });

        this.addFunc("get_scroll", _ -> valueOf(Mouse.getDWheel()));

    }

}
