package com.cosmos.server.core.rest.interceptor;

import com.cosmos.server.core.rest.HttpContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;

/**
 * Base filter class for HTTP request and response.
 *
 * @author BSD
 */
public abstract class HttpInterceptor {

    /**
     * http context of request information
     * <p>
     * user can update the value of context
     *
     * @param context http context
     * @return true if we continue the request or false will deny the request by http code 403
     */
    public boolean recvRequest(final HttpContext context) {
        return true;
    }

    /**
     * http context of response information
     * <p>
     * user can update the value of context or change the response
     * DO NOT accept null returned, it will be ignored
     *
     * @param context  http context
     * @param response response
     * @return response new response instance or current Object instance
     */
    public DefaultFullHttpResponse sendResponse(final HttpContext context, final DefaultFullHttpResponse response) {
        return response;
    }
}
