package cloud.popush.cache;


import java.lang.annotation.*;
import java.time.temporal.ChronoUnit;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheExpiring {
    int value();

    ChronoUnit unit() default ChronoUnit.SECONDS;
}
