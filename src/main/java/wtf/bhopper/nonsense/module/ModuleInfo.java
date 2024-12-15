package wtf.bhopper.nonsense.module;

import org.lwjglx.input.Keyboard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Apparently doing the modules this way messes up obfuscation...
// Oh well, good thing I'm not obfuscating!

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleInfo {

    String name();

    String description();

    ModuleCategory category();

    boolean toggled() default false;

    int bind() default Keyboard.KEY_NONE;

    boolean hidden() default false;

    String[] searchAlias() default {};

}
