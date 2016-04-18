package com.cosmos.server.core.http.impl.async;

import com.cosmos.server.core.http.HttpServerRouteProvider;

/**
 * {@link HttpServerRouteProvider} in Netty event loop.
 *
 * @author BSD
 */
public class AsyncHttpServerProvider extends HttpServerRouteProvider {

    // http protocol acceptor
    private final IOAcceptor ioAcceptor;

    /**
     * Async Http Server Provider Constructor.
     *
     * @param address address
     * @param port port
     */
    private AsyncHttpServerProvider(String address, int port) {
        this.ioAcceptor = new IOAcceptor(this, address, port);
    }

    /**
     * Creates a new {@link AsyncHttpServerProvider} with input parameters.
     *
     * @param address host address
     * @param port listening port
     * @return Async Http Server Provider Instance with Router
     */
    public static AsyncHttpServerProvider create(String address, Integer port) {
        return new AsyncHttpServerProvider(address, port);
    }

    @Override
    public Boolean start() {

        try {
            // run event loop and joined
            ioAcceptor.eventLoop();

            return Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }

    @Override
    public void join() throws InterruptedException {
        ioAcceptor.join();
    }

    @Override
    public void shutdown() {
        ioAcceptor.shutdown();
    }
}
