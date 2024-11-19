package wtf.bhopper.nonsense.module;

import org.lwjglx.input.Keyboard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleInfo {
    String name();
    ModuleCategory category();
    String description();
    int bind() default Keyboard.KEY_NONE;
}
