package com.cosmos.server.core.http.impl.async;

import com.cosmos.server.core.http.HttpServerStats;
import com.cosmos.server.core.http.rest.ControllerRouter;
import com.cosmos.server.core.http.rest.interceptor.HttpInterceptor;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.List;

/**
 * {@link SimpleChannelInboundHandler} with predefined {@link ControllerRouter} and {@link HttpInterceptor} list.
 *
 * @author BSD
 */
public abstract class AsyncRequestReceiver extends SimpleChannelInboundHandler<FullHttpRequest> {

    // resource controller route mapping
    protected static volatile ControllerRouter controllerRouter;

    // http interceptor list
    protected static volatile List<HttpInterceptor> interceptor;

    // Async workers
    protected static volatile ListeningExecutorService taskWorkerPool;

    /**
     * Setup Task Worker Pool with given parameter.
     *
     * @param workers number of worker
     */
    public static void newTaskPool(int workers) {
        taskWorkerPool = MoreExecutors.listeningDecorator(AsyncExecutors.newExecutors(workers));
    }

    /**
     * Setup resource controller route mapping.
     *
     * @param routeControllerMap resource controller route mapping
     */
    public static void useURLResourceController(ControllerRouter routeControllerMap) {
        controllerRouter = routeControllerMap;
    }

    /**
     * Setup http interceptor.
     *
     * @param interceptorList interceptor list
     */
    public static void useInterceptor(List<HttpInterceptor> interceptorList) {
        interceptor = interceptorList;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        HttpServerStats.incrConnections();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        HttpServerStats.decrConnections();
    }
}
