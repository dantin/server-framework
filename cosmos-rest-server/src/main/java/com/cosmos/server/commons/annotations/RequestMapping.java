package com.cosmos.server.commons.annotations;


import com.cosmos.server.commons.constant.http.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.cosmos.server.commons.constant.http.HttpConstants.HEADER_CONTENT_TYPE_JSON;
import static com.cosmos.server.commons.constant.http.HttpConstants.HEADER_CONTENT_TYPE_TEXT;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String value();

    RequestMethod method() default RequestMethod.GET;

    String consumes() default HEADER_CONTENT_TYPE_JSON;

    String produces() default HEADER_CONTENT_TYPE_TEXT;
}