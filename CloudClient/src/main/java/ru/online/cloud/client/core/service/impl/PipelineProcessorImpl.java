package ru.online.cloud.client.core.service.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.log4j.Log4j2;
import ru.online.cloud.client.core.handler.DataInboundHandler;
import ru.online.cloud.client.core.handler.FilesWriteHandler;
import ru.online.cloud.client.core.handler.parameter.FileParameter;
import ru.online.cloud.client.core.service.PipelineProcessor;
import ru.online.cloud.client.factory.Factory;
import ru.online.cloud.client.service.Callback;

@Log4j2
public class PipelineProcessorImpl implements PipelineProcessor {

    private static PipelineProcessorImpl instance;

    private PipelineProcessorImpl() {
    }

    public static PipelineProcessorImpl getInstance() {
        if (instance == null) {
            instance = new PipelineProcessorImpl();
        }
        return instance;
    }

    @Override
    public void clear(SocketChannel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        if (pipeline.get(ObjectDecoder.class) != null) {
            pipeline.remove(ObjectDecoder.class);
        }
        if (pipeline.get(ObjectEncoder.class) != null) {
            pipeline.remove(ObjectEncoder.class);
        }
        if (pipeline.get(DataInboundHandler.class) != null) {
            pipeline.remove(DataInboundHandler.class);
        }
        if (pipeline.get(ChunkedWriteHandler.class) != null) {
            pipeline.remove(ChunkedWriteHandler.class);
        }
        if (pipeline.get(FilesWriteHandler.class) != null) {
            pipeline.remove(FilesWriteHandler.class);
        }
        log.info("Pipeline cleared.");
    }

    @Override
    public void switchToCommand(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        if (pipeline.get(ObjectDecoder.class) == null) {
            pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
        }
        if (pipeline.get(ObjectEncoder.class) == null) {
            pipeline.addLast(new ObjectEncoder());
        }
        if (pipeline.get(DataInboundHandler.class) == null) {
            pipeline.addLast(new DataInboundHandler());
        }
        if (pipeline.get(ChunkedWriteHandler.class) == null) {
            pipeline.addLast(new ChunkedWriteHandler());
        }
        log.info("Pipeline changed to: " + ctx.pipeline());
    }

    @Override
    public void switchToFileUpload(SocketChannel channel, FileParameter fileParameter, Callback callback) {
        ChannelPipeline pipeline = channel.pipeline();
        if (pipeline.get(ObjectEncoder.class) == null) {
            pipeline.addLast(new ObjectEncoder());
        }
        if (pipeline.get(ChunkedWriteHandler.class) == null) {
            pipeline.addLast(new ChunkedWriteHandler());
        }
        if (pipeline.get(FilesWriteHandler.class) == null) {
            pipeline.addLast(new FilesWriteHandler(channel, this, Factory.getStorageService(), fileParameter, callback));
        }
        log.info("Pipeline changed to: " + channel.pipeline());
    }
}
