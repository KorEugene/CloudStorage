package ru.online.cloud.server.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.stream.ChunkedFile;
import lombok.extern.log4j.Log4j2;
import ru.online.cloud.server.core.service.PipelineProcessor;
import ru.online.cloud.server.service.DictionaryService;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

import java.io.File;
import java.io.IOException;

@Log4j2
public class CommandInboundHandler extends ChannelInboundHandlerAdapter {

    private final DictionaryService dictionaryService;
    private final PipelineProcessor pipelineProcessor;

    public CommandInboundHandler(DictionaryService dictionaryService, PipelineProcessor pipelineProcessor) {
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

        if (command.getCommandName() == CommandType.DOWNLOAD_READY) {
            loadFileToClient(ctx, command);
        } else {
            Command result = dictionaryService.processCommand(command);
            ctx.writeAndFlush(result);
            if (result.getCommandName() == CommandType.UPLOAD_READY) {
                pipelineProcessor.clear(ctx);
                pipelineProcessor.switchToFileUpload(ctx, (String) command.getArgs()[0], (Long) command.getArgs()[1], (String) command.getArgs()[2]);
            }
        }
    }

    private void loadFileToClient(ChannelHandlerContext ctx, Command command) {
        try {
            ChunkedFile chunkedFile = new ChunkedFile(new File(command.getPath()));
            ctx.channel().writeAndFlush(chunkedFile);
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }
    }
}
