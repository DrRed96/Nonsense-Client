package wtf.bhopper.nonsense.script.java;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

public class ClassObject extends SimpleJavaFileObject {

    private final String code;
    public final String name;

    public ClassObject(String name, String code) {
        super(URI.create("string:///" + name + ".java"), Kind.SOURCE);
        this.code = code;
        this.name = name;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return this.code;
    }
}
