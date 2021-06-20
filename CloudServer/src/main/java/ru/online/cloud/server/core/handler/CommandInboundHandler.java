package ru.online.cloud.server.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.online.domain.Command;
import ru.online.cloud.server.factory.Factory;
import ru.online.cloud.server.service.CommandDictionaryService;

public class CommandInboundHandler extends SimpleChannelInboundHandler<Command> {

    private CommandDictionaryService dictionaryService;

    public CommandInboundHandler() {
        this.dictionaryService = Factory.getCommandDirectoryService();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Подключился новый клиент: " + ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) {
        System.out.println("Получена команда: " + command.getCommandName());
        String result = dictionaryService.processCommand(command);
        ctx.writeAndFlush(result);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Клиент: " + ctx + " отключился");
    }
}
