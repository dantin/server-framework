package com.cosmos.netty.component.pipeline;

import com.cosmos.netty.Setting;
import com.cosmos.netty.component.mediator.Mediator;
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

    /**
     * 把共享的ExecutionHandler实例放在业务逻辑handler之前即可，注意ExecutionHandler一定要在不同的pipeline之间共享。
     * 它的作用是自动从ExecutionHandler自己管理的一个线程池中拿出一个线程来处理排在它后面的业务逻辑handler。
     * 而worker线程在经过ExecutionHandler后就结束了，它会被ChannelFactory的worker线程池所回收。
     * <p/>
     * 它的构造方法是ExecutionHandler(Executor executor)，很显然executor就是ExecutionHandler内部管理的线程池了。
     * Netty额外给我们提供了两种线程池：
     * MemoryAwareThreadPoolExecutor
     * OrderedMemoryAwareThreadPoolExecutor
     * 它们都在org.jboss.netty.handler.execution 包下。
     * MemoryAwareThreadPoolExecutor确保jvm不会因为过多的线程而导致内存溢出错误，
     * OrderedMemoryAwareThreadPoolExecutor是前一个线程池的子类，除了保证没有内存溢出之外，
     * 还可以保证channel event的处理次序。
     */
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
        pipeline.addLast("protobufDecoder", new ProtobufDecoder(Mediator.getInstance().getPbInstance()));

        pipeline.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast("protobufEncoder", new ProtobufEncoder());

        //心跳开关是否打开
        if (setting.isHeartBeatOn() && timer != null) {
            //心跳包 监听器
            pipeline.addLast("timeout", new IdleStateHandler(timer, setting.getHeartBeatReaderIdleTime(), setting.getHeartBeatWriterIdleTime(), setting.getHeartBeatAllIdleTime()));
            //心跳包
            pipeline.addLast("hearbeat", setting.getHeartBeatClass().newInstance());
        }

        if (executionHandler != null) {
            pipeline.addLast("executionHandler", executionHandler);
        }
        pipeline.addLast("bussinessHandler", setting.getBusinessHandlerClass().newInstance());

        return pipeline;
    }
}
