package wtf.bhopper.nonsense.script.java;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.script.ScriptOptionsMod;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

public class SecureClassLoader extends URLClassLoader {

    // Taken from Raven b4
    private static final List<String> UNSAFE_PACKAGES = Arrays.asList("java.nio",
            "java.net",
            "java.util.zip",
            "java.applet",
            "java.rmi",
            "java.security",
            "java.io.file",
            "java.lang.reflect",
            "java.lang.ref",
            "java.lang.thread",
            "java.io.buffer",
            "java.io.input",
            "java.io.read",
            "java.io.writer");

    public SecureClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        boolean isUnsafe = UNSAFE_PACKAGES.stream().anyMatch(name.toLowerCase()::startsWith) &&
                !name.endsWith("Exception") &&
                !Nonsense.module(ScriptOptionsMod.class).javaUnsafe.get();

        if (isUnsafe) {
            throw new ClassNotFoundException("Unsafe class detected: " + name);
        }

        return super.loadClass(name, resolve);

    }
}
