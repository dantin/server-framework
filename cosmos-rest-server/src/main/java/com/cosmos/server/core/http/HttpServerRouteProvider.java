package com.cosmos.server.core.http;

import com.cosmos.server.core.http.rest.interceptor.HttpInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * {@link HttpServerProvider} with {@link ControllerRouter} to dispatch http request.
 *
 * @author BSD
 */
public abstract class HttpServerRouteProvider extends HttpServerProvider {

    private static final Logger logger = LoggerFactory.getLogger(HttpServerRouteProvider.class);

    // http interceptors
    private static final List<HttpInterceptor> interceptors = new LinkedList<>();

    // http URI root path
    private static final String ROOT_PATH = "/";

    /**
     * Get http interceptors
     *
     * @return http interceptors
     */
    public List<HttpInterceptor> getInterceptor() {
        return interceptors;
    }
}
