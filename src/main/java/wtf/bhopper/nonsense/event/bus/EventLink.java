package wtf.bhopper.nonsense.event.bus;

import wtf.bhopper.nonsense.event.EventPriorities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EventLink {
    byte value() default EventPriorities.MEDIUM;
}
