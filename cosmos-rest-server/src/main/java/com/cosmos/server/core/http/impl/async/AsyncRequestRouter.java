package com.cosmos.server.core.http.impl.async;

import com.cosmos.server.commons.constant.http.RequestMethod;
import com.cosmos.server.core.http.HttpServerStats;
import com.cosmos.server.core.http.rest.ControllerRouter;
import com.cosmos.server.core.http.rest.ExecutorTask;
import com.cosmos.server.core.http.rest.HttpContext;
import com.cosmos.server.core.http.rest.URLResource;
import com.cosmos.server.core.http.rest.controller.URLController;
import com.cosmos.server.core.http.rest.request.NettyHttpRequestVisitor;
import com.cosmos.server.core.http.rest.response.HttpResponseBuilder;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.timeout.ReadTimeoutException;

/**
 * {@link AsyncRequestReceiver} which handler HTTP request via {@link ControllerRouter}.
 */
public class AsyncRequestRouter extends AsyncRequestReceiver {

    HttpContext httpContext;

    // channel context
    private ChannelHandlerContext context;

    /**
     * Creates a new {@code AsyncRequestRouter}.
     *
     * @return AsyncRequestRouter
     */
    public static AsyncRequestRouter build() {
        return new AsyncRequestRouter();
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest httpRequest) throws Exception {
        this.context = ctx;

        // build context
        httpContext = HttpContext.build(new NettyHttpRequestVisitor(context.channel(), httpRequest));

        // execute logic code
        doRun();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof ReadTimeoutException) {
            // http code 504
            if (context.channel().isOpen()) {
                sendResponse(HttpResponseBuilder.create(httpContext, HttpResponseStatus.GATEWAY_TIMEOUT));
            }
        }
    }

    /**
     * Execute business logic.
     */
    private void doRun() {

        /**
         * 1. checking phase. http method, param, url
         */
        if (!checkup()) {
            return;
        }

        /**
         * 2. according to URL to search the URLController
         */
        URLController controller = findController();

        /**
         * 3. execute controller logic to async executor thread pool
         */
        if (controller != null) {
            executeAsyncTask(controller);
        }
    }

    /**
     * Check http method.
     *
     * @return
     */
    private boolean checkup() {
        if (httpContext.getRequestMethod() == RequestMethod.UNKOWN) {
            // http code 405
            sendResponse(HttpResponseBuilder.create(httpContext, HttpResponseStatus.METHOD_NOT_ALLOWED));
            return false;
        }
        return true;
    }

    /**
     * Find the {@link URLController} for the incoming http request.
     *
     * @return target {@link URLController}, null if not found
     */
    private URLController findController() {
        // build URLResource from incoming http request
        URLResource resource = URLResource.fromHttp(httpContext.getUri(), httpContext.getRequestMethod());
        URLController controller;
        if ((controller = controllerRouter.findURLController(resource)) == null) {
            // http code 404
            sendResponse(HttpResponseBuilder.create(httpContext, HttpResponseStatus.NOT_FOUND));
            HttpServerStats.incrRequestMiss();
            return null;
        }

        if (!controller.isInternal()) {
            HttpServerStats.incrRequestHit();
            controller.hit();
        }

        return controller;
    }

    /**
     * Running Task asynchronously.
     *
     * @param controller
     */
    private void executeAsyncTask(URLController controller) {

        if (!controller.isInternal()) {
            HttpServerStats.setLastServTime(System.currentTimeMillis());
            HttpServerStats.setLastServID(httpContext.getRequestId());
        }

        // build logic task
        ExecutorTask task = new ExecutorTask(httpContext, interceptor, controller);

        Futures.addCallback(taskWorkerPool.submit(task), new FutureCallback<DefaultFullHttpResponse>() {
            @Override
            public void onSuccess(DefaultFullHttpResponse resp) {
                sendResponse(resp);
            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
                HttpServerStats.setLastServFailID(httpContext.getRequestId());
                // http code 503
                sendResponse(HttpResponseBuilder.create(httpContext, HttpResponseStatus.SERVICE_UNAVAILABLE));
            }
        });
    }

    /**
     * Write and close http response.
     *
     * @param response http response
     */
    private void sendResponse(DefaultFullHttpResponse response) {
        ChannelFuture future = context.channel().writeAndFlush(response);
        // http short connection
        if (!httpContext.isKeepAlive()) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
