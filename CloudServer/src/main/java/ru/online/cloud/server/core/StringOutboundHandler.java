package ru.online.cloud.server.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.nio.charset.StandardCharsets;

public class StringOutboundHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        String strMessage = (String) msg;

        ByteBuf buf = ctx.alloc().directBuffer();
        buf.writeBytes(strMessage.getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(buf);
    }
}
