package ru.online.cloud.client.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Setter;
import ru.online.cloud.client.service.Callback;
import ru.online.domain.Command;

public class DataInboundHandler extends SimpleChannelInboundHandler<Command> {

    @Setter
    private Callback incomingData;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) {
        if (incomingData != null) {
            incomingData.callback(command);
        }
    }
}
