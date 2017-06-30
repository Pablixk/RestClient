package gaguh.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by User on 29.06.2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) //on method level
public @interface Post {

    String path() default "";

}
