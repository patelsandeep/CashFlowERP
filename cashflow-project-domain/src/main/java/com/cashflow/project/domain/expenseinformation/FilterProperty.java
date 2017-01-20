package com.cashflow.project.domain.expenseinformation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * 
 * @since Nov 12, 2016, 3:19:54 PM
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FilterProperty {

    /**
     * If the filter name is different from the field name.
     */
    String value() default "";
}
