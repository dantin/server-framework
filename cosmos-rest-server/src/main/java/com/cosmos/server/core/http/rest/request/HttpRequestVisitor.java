package com.cosmos.server.core.http.rest.request;

import com.cosmos.server.commons.constant.http.RequestMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.util.Map;

/**
 * HTTP request visitor.
 *
 * @author BSD
 */
public interface HttpRequestVisitor {

    /**
     * Get the visitor's IP address.
     *
     * @return ip address
     */
    String visitRemoteAddress();

    /**
     * Get the request uri.
     *
     * @return uri
     */
    String visitURI();

    /**
     * Get uri terms, which is http uri terms split by "/".
     *
     * @return uri terms
     */
    String[] visitTerms();

    /**
     * Get the http {@link RequestMethod}.
     *
     * @return request method
     */
    RequestMethod visitHttpMethod();

    /**
     * Get the http request body.
     *
     * @return http body
     */
    String visitHttpBody();

    /**
     * Get the http request parameters.
     *
     * @return request parameter map
     */
    Map<String, String> visitHttpParams();

    /**
     * Get the http request header.
     *
     * @return request header map
     */
    Map<String, String> visitHttpHeaders();

    /**
     * Get the visitor's http version.
     *
     * @return http version
     */
    HttpVersion visitHttpVersion();
}
