package wtf.bhopper.nonsense.script.java;

import javax.tools.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class JavaEnvironment {

    public static final List<String> IMPORTS = Stream.of(
            Color.class,
            Collections.class,
            List.class,
            ArrayList.class,
            Arrays.class,
            Map.class,
            HashMap.class,
            HashSet.class,
            ConcurrentHashMap.class,
            LinkedHashMap.class,
            Iterator.class,
            Comparator.class,
            AtomicInteger.class,
            AtomicLong.class,
            AtomicBoolean.class,
            Random.class
    ).map(Class::getName).toList();

    private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    private final File tempDir;

    public JavaEnvironment() {
        this.tempDir = new File(System.getProperty("java.io.tmpdir"), "nonsense_jload");
    }

    private void parseFile(File file) {
        if (!file.getName().endsWith(".java")) {
            return;
        }

        String name = file.getName().replace(".java", "");
        if (name.isBlank()) {
            return;
        }

        StringBuilder code = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                code.append(line).append('\n');
            }
        } catch (IOException _) {}

        if (code.isEmpty()) {
            return;
        }

        JavaScript script = new JavaScript(file);
        script.create(code.toString());

    }

    public JavaCompiler getCompiler() {
        return this.compiler;
    }

    public StandardJavaFileManager getFileManager(ScriptDiagnosticListener diagnostic) {
        return this.compiler.getStandardFileManager(diagnostic, null, null);
    }

    public File getTempDir() {
        return this.tempDir;
    }

}
