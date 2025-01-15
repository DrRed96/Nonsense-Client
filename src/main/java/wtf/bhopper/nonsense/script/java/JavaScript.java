package wtf.bhopper.nonsense.script.java;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.script.Script;

import javax.tools.StandardJavaFileManager;
import java.io.File;
import java.util.Arrays;
import java.util.List;

// lol js
// I love js lol
// js my favorite language!
public class JavaScript extends Script {

    private Class<Object> clazz;
    private boolean error = false;

    public JavaScript(File file) {
        super(file.getName());
    }

    public boolean run() {
        try {

            ScriptDiagnosticListener diagnostic = new ScriptDiagnosticListener();
            StandardJavaFileManager fileManager = env().getFileManager(diagnostic);
            List<String> options = Arrays.asList(
                    "-d", env().getTempDir().getAbsolutePath(),
                    "-XDuseUnsharedTable"
            );

//            env().getCompiler().getTask(null, fileManager, diagnostic, options, null, Arrays.asList(new ClassObject()));

        } catch (Exception exception) {
            this.error = true;
        }

        return false;

    }

    public void create(String code) {
        StringBuilder builder = new StringBuilder();
        for (String clazz : JavaEnvironment.IMPORTS) {
            builder.append("import ")
                    .append(clazz)
                    .append(";\n");
        }
    }

    public static JavaEnvironment env() {
        return Nonsense.getScriptManager().getJavaEnv();
    }

}
