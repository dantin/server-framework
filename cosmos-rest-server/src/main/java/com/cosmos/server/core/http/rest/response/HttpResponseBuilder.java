package com.cosmos.server.core.http.rest.response;

import com.cosmos.server.commons.constant.NettyServerConstants;
import com.cosmos.server.commons.constant.http.HttpConstants;
import com.cosmos.server.commons.utils.SerializeUtils;
import com.cosmos.server.core.http.rest.HttpContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * Builder that simplify the creation process of {@link DefaultFullHttpResponse} object.
 */
public class HttpResponseBuilder {

    // if no content in DefaultFullHttpResponse we will fill empty body
    private static final byte[] uselessBuffer = SerializeUtils.encode(new EMPTY());

    /**
     * Create a new {@link DefaultFullHttpResponse} via {@link HttpResponseStatus}.
     *
     * @param httpContext http context
     * @param status HTTP Status
     * @return HTTP response
     */
    public static DefaultFullHttpResponse create(HttpContext httpContext, HttpResponseStatus status) {
        return create(httpContext, status, Unpooled.wrappedBuffer(uselessBuffer));
    }

    /**
     * Create a new {@link DefaultFullHttpResponse} via {@link ByteBuf}.
     *
     * @param httpContext http context
     * @param content byte content
     * @return HTTP response
     */
    public static DefaultFullHttpResponse create(HttpContext httpContext, ByteBuf content) {
        return create(httpContext, HttpResponseStatus.OK, content);
    }

    /**
     * Create a new {@link DefaultFullHttpResponse} via {@link HttpResponseStatus} and {@link ByteBuf}.
     *
     * @param httpContext http context
     * @param status HTTP Status
     * @param content byte content
     * @return HTTP response
     */
    public static DefaultFullHttpResponse create(HttpContext httpContext, HttpResponseStatus status, ByteBuf content) {
        DefaultFullHttpResponse resp;
        if (content != null) {
            resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
            resp.headers().set(HttpConstants.HEADER_CONTENT_LENGTH, content.readableBytes());
            resp.headers().set(HttpConstants.HEADER_CONTENT_TYPE, HttpConstants.HEADER_CONTENT_TYPE_JSON);
        } else {
            resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
        }

        resp.headers().set(HttpConstants.HEADER_REQUEST_ID, httpContext.getRequestId());
        resp.headers().set(HttpConstants.HEADER_SERVER, NettyServerConstants.NETTY_SERVER);
        resp.headers().set(HttpConstants.HEADER_CONNECTION, HttpConstants.HEADER_CONNECTION_KEEP_ALIVE);
        return resp;
    }

    static class EMPTY {

    }
}
