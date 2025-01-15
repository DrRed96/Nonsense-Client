package wtf.bhopper.nonsense.script.java;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

public class ScriptDiagnosticListener implements DiagnosticListener<JavaFileObject> {
    @Override
    public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
        String message = diagnostic.getMessage(null);
    }
}
