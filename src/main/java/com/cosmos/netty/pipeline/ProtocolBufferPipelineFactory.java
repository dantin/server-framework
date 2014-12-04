package com.cosmos.netty.pipeline;

import com.cosmos.server.Setting;
import com.cosmos.netty.mediator.AbstractMediator;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.protobuf.ProtobufDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufEncoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.Timer;

/**
 *
 * @author David
 */
public class ProtocolBufferPipelineFactory implements ChannelPipelineFactory {

    private final ExecutionHandler executionHandler;

    private Timer timer;

    private final Setting setting;

    public ProtocolBufferPipelineFactory(final Setting setting, ExecutionHandler executionHandler) {
        this.executionHandler = executionHandler;
        this.setting = setting;
    }

    public ProtocolBufferPipelineFactory(Setting setting, ExecutionHandler executionHandler, Timer timer) {
        this.executionHandler = executionHandler;
        this.timer = timer;
        this.setting = setting;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
        pipeline.addLast("protobufDecoder", new ProtobufDecoder(AbstractMediator.getInstance().getPbInstance()));

        pipeline.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast("protobufEncoder", new ProtobufEncoder());

        //心跳开关是否打开
        if (setting.isHeartBeatOn() && timer != null) {
            //心跳包 监听器
            pipeline.addLast("timeout", new IdleStateHandler(timer, setting.getHeartBeatReaderIdleTime(), setting.getHeartBeatWriterIdleTime(), setting.getHeartBeatAllIdleTime()));
            //心跳包
            pipeline.addLast("hearbeat", setting.getHeartBeatClass().newInstance());
        }

        if (executionHandler != null)
            pipeline.addLast("executionHandler", executionHandler);

        pipeline.addLast("bussinessHandler", setting.getBusinessHandlerClass().newInstance());

        return pipeline;
    }
}
