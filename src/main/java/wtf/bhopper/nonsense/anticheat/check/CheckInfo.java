package wtf.bhopper.nonsense.anticheat.check;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckInfo {

    String name();
    String description();
    int maxVl();
    boolean enabled() default true;

}
