package ru.online.cloud.client.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import ru.online.domain.Command;

public class CommandOutboundHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object command, ChannelPromise promise) {
        Command com = (Command) command;
        ctx.writeAndFlush(com);
//        super.write(ctx, command, promise);
    }
}
