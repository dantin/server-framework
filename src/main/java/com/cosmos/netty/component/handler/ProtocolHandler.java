package com.cosmos.netty.component.handler;

import com.cosmos.netty.component.mediator.Mediator;
import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetSocketAddress;

/**
 * Protocol Handler的处理类
 *
 * @author David
 */
public class ProtocolHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProtocolHandler.class);

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        super.handleUpstream(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Channel channel = e.getChannel();
        InetSocketAddress address = (InetSocketAddress) channel.getRemoteAddress();
        Inet4Address net4 = (Inet4Address) address.getAddress();
        String ip = net4.getHostAddress();

        Object request = e.getMessage();
        if (request != null) {
            Object response = Mediator.getInstance().execute(request, ip, ctx, e);
            if (response == null) {
                logger.error("execute business return object is null, please check business method and return one object.");
            } else {
                if (channel.isConnected()) {
                    channel.write(response);
                } else {
                    logger.warn("Failed to write any response because the channel is not connected any more. Maybe the client has closed the connection?");
                }
            }
        } else {
            logger.error("message received is null, please check send request");
        }
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        Channel channel = e.getChannel();
        InetSocketAddress address = (InetSocketAddress) channel.getRemoteAddress();
        logger.info("a channelConnected ...." + address.getAddress() + " : " + address.getPort());

        super.channelConnected(ctx, e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        logger.error("do extension error : ", e.getCause());
        e.getChannel().close().awaitUninterruptibly();
    }
}
