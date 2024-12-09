package wtf.bhopper.nonsense.script;

import java.io.File;

public class Script {
    public final File file;
    public final Type type;
    private boolean enabled;

    public Script(File file, boolean enabled) {
        this.file = file;
        this.enabled = enabled;

        if (this.file.getName().endsWith(".lua")) {
            this.type = Type.LUA;
        } else {
            throw new IllegalArgumentException("Unknown script type");
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public enum Type {
        LUA
    }
}
