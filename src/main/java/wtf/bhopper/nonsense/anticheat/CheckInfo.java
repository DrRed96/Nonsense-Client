package wtf.bhopper.nonsense.anticheat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckInfo {
    String name();
    String classifier();
    String description();
    int maxViolations() default 20;
    boolean unreliable() default false;
}
