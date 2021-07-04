package ru.online.cloud.server.core.service;

import io.netty.channel.ChannelHandlerContext;

public interface PipelineProcessor {

    void clear(ChannelHandlerContext ctx);

    void switchToCommand(ChannelHandlerContext ctx);

    void switchToFileUpload(ChannelHandlerContext ctx, String fileName, long fileSize, String userDir);
}
