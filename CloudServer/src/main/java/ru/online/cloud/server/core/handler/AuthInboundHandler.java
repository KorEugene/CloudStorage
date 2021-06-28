package ru.online.cloud.server.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;
import ru.online.cloud.server.factory.Factory;
import ru.online.cloud.server.service.CommandDictionaryService;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

public class AuthInboundHandler extends ChannelInboundHandlerAdapter {

    private CommandDictionaryService dictionaryService;
    private SocketChannel channel;

    public AuthInboundHandler(SocketChannel channel) {
        this.dictionaryService = Factory.getCommandDirectoryService();
        this.channel = channel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Подключился клиент: " + ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Отключился клиент: " + ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        Command command = (Command) msg;

        if (command.getCommandName() == CommandType.REGISTRATION_REQUEST) {
            Command result = dictionaryService.processCommand(command);
            ctx.writeAndFlush(result);
        }

        if (command.getCommandName() == CommandType.AUTH_REQUEST) {
            Command result = dictionaryService.processCommand(command);
            ctx.writeAndFlush(result);
            switchToCommandPipeline(channel);
        }

    }

    private void switchToCommandPipeline(SocketChannel channel) {
        ChannelPipeline p = channel.pipeline();
        if (p.get("command") == null) {
            p.addLast("command", new CommandInboundHandler(channel));
        }
        if (p.get("chunkWr") == null) {
            p.addLast("chunkWr", new ChunkedWriteHandler());
        }
        p.remove(this);
    }

}
