package com.cosmos.server.core.http;

/**
 * A abstract class that implement {@link HttpServer}, it's allows to be customized by {@link HttpServerOptions}.
 *
 * @author BSD
 */
public abstract class HttpServerProvider implements HttpServer {

    // options contained by HttpServerProvider
    private HttpServerOptions options = new HttpServerOptions();

    /**
     * Specify a new {@link HttpServerOptions} to the current {@link HttpServerProvider}.
     *
     * @param options http server options
     * @return {@link HttpServerProvider} with specified {@link HttpServerOptions}
     */
    public HttpServerProvider useOptions(HttpServerOptions options) {
        this.options = options;
        return this;
    }

    /**
     * Get the using {@link HttpServerOptions}.
     *
     * @return current using http server options
     */
    public HttpServerOptions options() {
        return options;
    }

}
