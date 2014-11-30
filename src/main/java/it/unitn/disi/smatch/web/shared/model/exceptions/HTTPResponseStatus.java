package it.unitn.disi.smatch.web.shared.model.exceptions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HTTPResponseStatus {

    /**
     * HTTP Status code
     *
     * @return HTTP Status code
     */
    int value() default 500;
}