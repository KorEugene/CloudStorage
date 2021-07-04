package ru.online.cloud.server.core.service.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.log4j.Log4j2;
import ru.online.cloud.server.core.handler.AuthInboundHandler;
import ru.online.cloud.server.core.handler.CommandInboundHandler;
import ru.online.cloud.server.core.handler.FilesWriteHandler;
import ru.online.cloud.server.core.service.PipelineProcessor;
import ru.online.cloud.server.factory.Factory;

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
    public void clear(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        if (pipeline.get(ObjectDecoder.class) != null) {
            pipeline.remove(ObjectDecoder.class);
        }
        if (pipeline.get(ObjectEncoder.class) != null) {
            pipeline.remove(ObjectEncoder.class);
        }
        if (pipeline.get(AuthInboundHandler.class) != null) {
            pipeline.remove(AuthInboundHandler.class);
        }
        if (pipeline.get(CommandInboundHandler.class) != null) {
            pipeline.remove(CommandInboundHandler.class);
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
        if (pipeline.get(CommandInboundHandler.class) == null) {
            pipeline.addLast(new CommandInboundHandler(Factory.getDictionaryService(Factory.getCommandServices()), this));
        }
        if (pipeline.get(ChunkedWriteHandler.class) == null) {
            pipeline.addLast(new ChunkedWriteHandler());
        }
        log.info("Pipeline changed to: " + ctx.pipeline());
    }

    @Override
    public void switchToFileUpload(ChannelHandlerContext ctx, String fileName, long fileSize, String userDir) {
        ChannelPipeline pipeline = ctx.pipeline();
        if (pipeline.get(ObjectEncoder.class) == null) {
            pipeline.addLast(new ObjectEncoder());
        }
        if (pipeline.get(ChunkedWriteHandler.class) == null) {
            pipeline.addLast(new ChunkedWriteHandler());
        }
        if (pipeline.get(FilesWriteHandler.class) == null) {
            pipeline.addLast(new FilesWriteHandler(this, Factory.getStorageService(), fileName, fileSize, userDir));
        }
        log.info("Pipeline changed to: " + ctx.pipeline());
    }
}
