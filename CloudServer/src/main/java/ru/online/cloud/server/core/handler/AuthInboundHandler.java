package ru.online.cloud.server.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j2;
import ru.online.cloud.server.core.service.PipelineProcessor;
import ru.online.cloud.server.service.DictionaryService;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

@Log4j2
public class AuthInboundHandler extends ChannelInboundHandlerAdapter {

    private final DictionaryService dictionaryService;
    private final PipelineProcessor pipelineProcessor;

    public AuthInboundHandler(DictionaryService dictionaryService, PipelineProcessor pipelineProcessor) {
        this.dictionaryService = dictionaryService;
        this.pipelineProcessor = pipelineProcessor;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("Client connected: " + ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("Client disconnected: " + ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        Command command = (Command) msg;

        Command result = dictionaryService.processCommand(command);
        ctx.writeAndFlush(result);
        if (result.getCommandName() == CommandType.AUTH_SUCCEEDED) {
            pipelineProcessor.clear(ctx);
            pipelineProcessor.switchToCommand(ctx);
        }
    }
}
