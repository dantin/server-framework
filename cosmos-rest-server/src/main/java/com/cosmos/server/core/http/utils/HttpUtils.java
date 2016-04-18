package com.cosmos.server.core.http.utils;

import com.cosmos.server.commons.constant.http.RequestMethod;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Http utilities.
 *
 * @author BSD
 */
public class HttpUtils {

    /**
     * Get the {@link RequestMethod} from http request.
     *
     * @param request http request
     * @return {@link RequestMethod}
     */
    public static RequestMethod convertHttpMethodFromNetty(FullHttpRequest request) {
        try {
            return RequestMethod.valueOf(request.getMethod().name().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return RequestMethod.UNKOWN;
        }
    }

    /**
     * Get the base URL String, substring before '?'
     *
     * @param url URL String
     * @return base URL String
     */
    public static String truncateUrl(String url) {
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        return url;
    }
}
