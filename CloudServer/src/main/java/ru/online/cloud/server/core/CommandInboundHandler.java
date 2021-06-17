package ru.online.cloud.server.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.online.cloud.server.factory.Factory;
import ru.online.cloud.server.service.CommandDictionaryService;

public class CommandInboundHandler extends SimpleChannelInboundHandler<String> {

    private CommandDictionaryService dictionaryService;

    public CommandInboundHandler() {
        this.dictionaryService = Factory.getCommandDirectoryService();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String command) {
        String result = dictionaryService.processCommand(command);
        ctx.writeAndFlush(result);
    }
}
