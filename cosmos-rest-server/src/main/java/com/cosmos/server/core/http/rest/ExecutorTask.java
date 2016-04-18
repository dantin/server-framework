package com.cosmos.server.core.http.rest;

import com.cosmos.server.commons.utils.SerializeUtils;
import com.cosmos.server.core.http.rest.controller.URLController;
import com.cosmos.server.core.http.rest.interceptor.HttpInterceptor;
import com.cosmos.server.core.http.rest.response.HttpResult;
import com.cosmos.server.core.http.rest.response.HttpResponseBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * A {@link Callable} that execute business logic.
 */
public class ExecutorTask implements Callable<DefaultFullHttpResponse> {

    // http httpContext used for controller invoking
    private final HttpContext httpContext;
    // related controller
    private final URLController handler;
    // interceptor list
    private final List<HttpInterceptor> interceptors;

    /**
     *
     * @param httpContext
     * @param interceptors
     * @param handler
     */
    public ExecutorTask(HttpContext httpContext, List<HttpInterceptor> interceptors, URLController handler) {
        this.httpContext = httpContext;
        this.interceptors = interceptors;
        this.handler = handler;
    }

    @Override
    public DefaultFullHttpResponse call() {
        // call interceptors chain of recvRequest
        for (HttpInterceptor every : interceptors) {
            if (!every.recvRequest(httpContext))
                return HttpResponseBuilder.create(httpContext, HttpResponseStatus.FORBIDDEN);        // http code 403
        }

        // call controller method
        HttpResult result = handler.call(httpContext);

        DefaultFullHttpResponse response;

        switch (result.getHttpStatus()) {
            case SUCCESS:
                if (result.getHttpContent() != null) {
                    ByteBuf content = Unpooled.wrappedBuffer(SerializeUtils.encode(result.getHttpContent()));
                    response = HttpResponseBuilder.create(httpContext, content);                        // http code 200
                } else
                    response = HttpResponseBuilder.create(httpContext, HttpResponseStatus.NO_CONTENT);  // http code 204
                break;
            case RESPONSE_NOT_VALID:
                response = HttpResponseBuilder.create(httpContext, HttpResponseStatus.BAD_GATEWAY);     // http code 502
                break;
            case PARAMS_CONVERT_ERROR:
            case PARAMS_NOT_MATCHED:
                // http code 400
                response = HttpResponseBuilder.create(httpContext, HttpResponseStatus.BAD_REQUEST);
                break;
            case SYSTEM_ERROR:
                // http code 500
                response = HttpResponseBuilder.create(httpContext, HttpResponseStatus.INTERNAL_SERVER_ERROR);
                break;
            default:
                // http code 500
                response = HttpResponseBuilder.create(httpContext, HttpResponseStatus.INTERNAL_SERVER_ERROR);
                break;
        }

        // call interceptor chain of sendResponse. returned DefaultFullHttpResponse
        // will be replaced to original instance
        for (HttpInterceptor every : interceptors) {
            DefaultFullHttpResponse newResponse = every.sendResponse(httpContext, response);
            if (newResponse != null)
                response = newResponse;
        }

        return response;
    }
}
