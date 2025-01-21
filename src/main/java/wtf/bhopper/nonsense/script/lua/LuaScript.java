package wtf.bhopper.nonsense.script.lua;

import wtf.bhopper.nonsense.script.AbstractScript;

import java.io.File;

public class LuaScript extends AbstractScript {

    private final File file;

    public File getFile() {
        return this.file;
    }

    public LuaScript(File file) {
        super(file.getName());
        this.file = file;
    }

}
