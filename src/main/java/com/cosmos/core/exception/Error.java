package com.cosmos.core.exception;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Error记录详情
 *
 * @author David
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Error {

    String message() default "";

    String code() default "";

}
