package ru.online.cloud.server.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.online.cloud.server.factory.Factory;
import ru.online.cloud.server.service.CommandDictionaryService;
import ru.online.domain.Command;
import ru.online.domain.CommandType;

public class CommandInboundHandler extends ChannelInboundHandlerAdapter {

    private CommandDictionaryService dictionaryService;

    public CommandInboundHandler() {
        this.dictionaryService = Factory.getCommandDirectoryService();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Подключился новый клиент: " + ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Command command = (Command) msg;
        System.out.println("Получена команда: " + command.getCommandName());
//        java.lang.String result = dictionaryService.processCommand(command);
//        Command result = new Command(CommandType.LS_CURRENT, new Object[]{dictionaryService.processCommand(command)});
        Command result = dictionaryService.processCommand(command);
//        Command result = new Command(CommandType.LS_RESULT, new String[]{dictionaryService.processCommand(command)});
        ctx.writeAndFlush(result);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Клиент: " + ctx + " отключился");
    }
}
