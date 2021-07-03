package ru.online.cloud.server.core.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.log4j.Log4j2;
import ru.online.cloud.server.factory.Factory;
import ru.online.cloud.server.service.CommandDictionaryService;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

@Log4j2
public class AuthInboundHandler extends ChannelInboundHandlerAdapter {

    private final CommandDictionaryService dictionaryService;

    public AuthInboundHandler() {
        this.dictionaryService = Factory.getCommandDirectoryService();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("Подключился клиент: " + ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("Отключился клиент: " + ctx);
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
            if (result.getCommandName() == CommandType.AUTH_SUCCEEDED) {
                switchToCommandPipeline(ctx.channel());
            }
        }

    }

    private void switchToCommandPipeline(Channel channel) {
        ChannelPipeline p = channel.pipeline();
        if (p.get(CommandInboundHandler.class) == null) {
            p.addLast(new CommandInboundHandler());
        }
        if (p.get(ChunkedWriteHandler.class) == null) {
            p.addLast(new ChunkedWriteHandler());
        }
        p.remove(this);
        log.info("Pipeline changed to: " + p);
    }

}
