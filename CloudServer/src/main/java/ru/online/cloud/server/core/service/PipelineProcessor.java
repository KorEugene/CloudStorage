package ru.online.cloud.server.core.service;

import io.netty.channel.ChannelHandlerContext;
import ru.online.cloud.server.core.handler.parameter.FileParameter;

public interface PipelineProcessor {

    void clear(ChannelHandlerContext ctx);

    void switchToCommand(ChannelHandlerContext ctx);

    void switchToFileUpload(ChannelHandlerContext ctx, FileParameter fileParameter);
}
