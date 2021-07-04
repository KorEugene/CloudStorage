package ru.online.cloud.client.core.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import ru.online.cloud.client.core.handler.parameter.FileParameter;
import ru.online.cloud.client.service.Callback;

public interface PipelineProcessor {

    void clear(SocketChannel channel);

    void switchToCommand(ChannelHandlerContext ctx);

    void switchToFileUpload(SocketChannel channel, FileParameter fileParameter, Callback callback);
}
