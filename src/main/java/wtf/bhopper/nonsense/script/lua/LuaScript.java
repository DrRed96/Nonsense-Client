package wtf.bhopper.nonsense.script.lua;

import wtf.bhopper.nonsense.script.Script;

import java.io.File;

public class LuaScript extends Script {

    private final File file;

    public File getFile() {
        return this.file;
    }

    public LuaScript(File file) {
        super(file.getName());
        this.file = file;
    }

}
