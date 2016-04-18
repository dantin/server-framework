package com.cosmos.server.core.http.rest.controller;

import com.cosmos.server.commons.exceptions.ControllerParamsNotMatchException;
import com.cosmos.server.commons.exceptions.ControllerParamsParsedException;
import com.cosmos.server.core.http.impl.async.HttpResultStatus;
import com.cosmos.server.core.http.rest.HttpContext;
import com.cosmos.server.core.http.rest.response.HttpResult;
import com.cosmos.server.core.http.utils.HitCounter;

import com.google.common.base.MoreObjects;

import java.lang.reflect.Method;

/**
 * {@link HitCounter} subclass that contains the relationship between URI, Controller class and method.
 */
public class URLController extends HitCounter {

    // target class
    private ControllerClassDescriptor provider;

    // target method
    private ControllerMethodDescriptor procedure;

    // internal controller sign. only for root-path
    private boolean internal = false;

    private URLController() {
        // do nothing here
    }

    /**
     * Create new {@link URLController} via input parameters.
     *
     * @param uri uri string
     * @param providerClass  Controller class
     * @param procedure Controller method
     * @return URLController
     */
    public static URLController fromProvider(String uri, Class<?> providerClass, Method procedure) {
        URLController handler = new URLController();
        handler.provider = new ControllerClassDescriptor(providerClass);
        handler.procedure = new ControllerMethodDescriptor(uri, handler.provider, procedure);
        return handler;
    }

    /**
     * Set for internal use
     *
     * @return URLController
     */
    public URLController internal() {
        this.internal = true;
        return this;
    }

    /**
     * If this URLController is for internal use
     *
     * @return true if internal, false otherwise
     */
    public boolean isInternal() {
        return this.internal;
    }

    public HttpResult call(HttpContext context) {
        /**
         * make new controller class instance with every http request. because
         * of we desire every request may has own context variables and status.
         *
         * TODO : This newInstance() need a empty param default constructor.
         *
         */
        try {
            Object result = procedure.invoke(context);
            if (result != null && !result.getClass().isPrimitive())
                return new HttpResult(HttpResultStatus.SUCCESS, result);
            else
                return new HttpResult(HttpResultStatus.RESPONSE_NOT_VALID);
        } catch (ControllerParamsNotMatchException e) {
            return new HttpResult(HttpResultStatus.PARAMS_NOT_MATCHED);
        } catch (ControllerParamsParsedException e) {
            return new HttpResult(HttpResultStatus.PARAMS_CONVERT_ERROR);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(URLController.class).omitNullValues()
                .add("provider", provider.getClazz().getCanonicalName())
                .add("procedure", procedure.getMethod().getName()).toString();
    }
}
