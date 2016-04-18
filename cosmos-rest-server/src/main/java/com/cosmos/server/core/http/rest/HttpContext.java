package com.cosmos.server.core.http.rest;

import com.cosmos.server.commons.constant.http.RequestMethod;
import com.cosmos.server.core.http.rest.request.HttpRequestVisitor;
import io.netty.handler.codec.http.HttpVersion;

import java.util.Map;
import java.util.UUID;

import static com.cosmos.server.commons.constant.http.HttpConstants.HEADER_CONNECTION_CLOSE;
import static com.cosmos.server.commons.constant.http.HttpConstants.HEADER_CONNECTION;
import static com.cosmos.server.commons.constant.http.HttpConstants.HEADER_CONNECTION_KEEP_ALIVE;

/**
 * {@link HttpSession} include all http request and response information.
 *
 * @author BSD
 */
public class HttpContext extends HttpSession {

    // raw http body
    protected String httpBody;

    // params include query string and body params
    protected Map<String, String> httpParams;

    // attribute map values
    protected Map<String, Object> httpAttributes;

    // header values
    protected Map<String, String> httpHeaders;

    // client request with unique id, fetch it from http header(Request-Id)
    // if exists, or generate by UUID
    private String requestId = UUID.randomUUID().toString();

    // ip address from origin client, fetch it from getRemoteAddr()
    // or header X-FORWARDED-FOR
    private String remoteAddress;

    // raw uri exclude query string
    private String uri;

    // http uri terms split by "/"
    private String[] terms;

    // Http request method. NOT null
    private RequestMethod requestMethod;

    // Http long connection
    private boolean isKeepAlive = true;

    private HttpContext() {
    }

    /**
     * Creates a new {@link HttpContext} with the given parameters.
     *
     * @param visitor request visitor
     * @return http context
     */
    public static HttpContext build(HttpRequestVisitor visitor) {
        HttpContext context = new HttpContext();
        context.remoteAddress = visitor.visitRemoteAddress();
        context.uri = visitor.visitURI();
        context.terms = visitor.visitTerms();
        context.requestMethod = visitor.visitHttpMethod();
        context.httpHeaders = visitor.visitHttpHeaders();
        context.httpParams = visitor.visitHttpParams();

        // TODO : if exclude GET or not ?
        //
        context.httpBody = visitor.visitHttpBody();

        if (visitor.visitHttpVersion() == HttpVersion.HTTP_1_1 &&
                HEADER_CONNECTION_CLOSE.equals(context.httpHeaders.get(HEADER_CONNECTION)))
            context.isKeepAlive = false;

        if (visitor.visitHttpVersion() == HttpVersion.HTTP_1_0 &&
                !HEADER_CONNECTION_KEEP_ALIVE.equalsIgnoreCase(context.httpHeaders.get(HEADER_CONNECTION)))
            context.isKeepAlive = false;

        return context;
    }

    public boolean isKeepAlive() {
        return isKeepAlive;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public String getUri() {
        return uri;
    }

    public String[] getTerms() {
        return terms;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public String getHttpBody() {
        return httpBody;
    }

    @Override
    public Map<String, String> getHttpParams() {
        return httpParams;
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    @Override
    public String getRequestId() {
        return requestId;
    }

    @Override
    public Map<String, Object> getHttpAttributes() {
        return httpAttributes;
    }
}
