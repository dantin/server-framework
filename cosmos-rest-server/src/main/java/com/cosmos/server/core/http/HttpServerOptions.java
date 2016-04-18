package com.cosmos.server.core.http;

/**
 * Customized HTTP Server Options.
 *
 * @author BSD
 */
public class HttpServerOptions {

    // max connections in this http server instance.
    //
    // incoming http connection over the number of maxConnections
    // will be reject and return http code 503
    private int maxConnections = 10000;

    // max received packet size.
    //
    // default is 16MB
    private int maxPacketSize = 16 * 1024 * 1024;

    // network socket io threads number.
    //
    // default is cpu core - 1
    // DO NOT set the number more than cpu core number
    private int ioThreads = Runtime.getRuntime().availableProcessors() - 1;

    // logic handler threads number.
    //
    // default is 128
    // adjust the ioThreads number bigger if there are more critical region code or block code
    // in your handler logic (io intensive);
    // and smaller if your code has no block almost (cpu intensive)
    private int handlerThreads = 128;

    // logic handler timeout.
    //
    // default is 30s
    private int handleTimeout = 30 * 1000;

    /**
     * Get max connections in this http server instance.
     *
     * @return max connection
     */
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     * Configure max client connections at the same time.
     * <p>
     * {@link HttpServer} will return http code <code>503</code> when connection is too much
     *
     * @param maxConnections max connections number
     */
    public HttpServerOptions setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
        return this;
    }

    /**
     * Get network socket io threads number.
     *
     * @return network socket io threads number
     */
    public int getIoThreads() {
        return ioThreads;
    }

    /**
     * Configure network socket io threads pool size.
     *
     * @param ioThreads max io threads number
     */
    public HttpServerOptions setIoThreads(int ioThreads) {
        this.ioThreads = ioThreads;
        return this;
    }

    /**
     * Get logic handler threads number.
     *
     * @return logic handler threads number
     */
    public int getHandlerThreads() {
        return handlerThreads;
    }

    /**
     * Configure user logic threads pool size.
     *
     * @param handlerThreads max user logic threads number
     */
    public HttpServerOptions setHandlerThreads(int handlerThreads) {
        this.handlerThreads = handlerThreads;
        return this;
    }

    /**
     * Get logic handler timeout of a client connection.
     *
     * @return logic handler timeout
     */
    public int getHandlerTimeout() {
        return handleTimeout;
    }

    /**
     * Configure client connection's max handle timeout.
     * <p>
     * {@link HttpServer} will close the connection with http code <code>504</code> if processing time is longer
     * than this threshold.
     *
     * @param handleTimeout max connections number
     */
    public HttpServerOptions setHandlerTimeout(int handleTimeout) {
        this.handleTimeout = handleTimeout;
        return this;
    }

    /**
     * Get the max received packet size.
     *
     * @return max received packet size
     */
    public int getMaxPacketSize() {
        return maxPacketSize;
    }

    /**
     * Configure http max packet length limit.
     *
     * @param maxPacketSize max http packet size
     */
    public HttpServerOptions setMaxPacketSize(int maxPacketSize) {
        this.maxPacketSize = maxPacketSize;
        return this;
    }

}
