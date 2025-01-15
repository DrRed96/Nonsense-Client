package wtf.bhopper.nonsense.script.lua.api;

import org.luaj.vm2.Varargs;

public interface ILuaFunction {
    Varargs invoke(Varargs args);
}
