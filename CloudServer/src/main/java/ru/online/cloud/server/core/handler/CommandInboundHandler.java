package ru.online.cloud.server.core.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.log4j.Log4j2;
import ru.online.cloud.server.factory.Factory;
import ru.online.cloud.server.service.CommandDictionaryService;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

import java.io.File;
import java.io.IOException;

@Log4j2
public class CommandInboundHandler extends ChannelInboundHandlerAdapter {

    private final CommandDictionaryService dictionaryService;

    public CommandInboundHandler() {
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

        if (command.getCommandName() == CommandType.LS) {
            processLSCommand(ctx, command);
        }

        if (command.getCommandName() == CommandType.UPLOAD) {
            Command result = dictionaryService.processCommand(command);
            ctx.writeAndFlush(result);
            switchToFileUploadPipeline(ctx.channel(), (String) command.getArgs()[0], (Long) command.getArgs()[1], (String) command.getArgs()[2]);
        }

        if (command.getCommandName() == CommandType.DOWNLOAD || command.getCommandName() == CommandType.MK_DIR) {
            Command result = dictionaryService.processCommand(command);
            ctx.writeAndFlush(result);
        }

        if (command.getCommandName() == CommandType.DOWNLOAD_READY) {
            try {
                ChunkedFile chunkedFile = new ChunkedFile(new File(command.getPath()));
                ctx.channel().writeAndFlush(chunkedFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processLSCommand(ChannelHandlerContext ctx, Command command) {
        Command result = dictionaryService.processCommand(command);
        ctx.writeAndFlush(result);
    }

    private void switchToFileUploadPipeline(Channel channel, String fileName, long fileSize, String userDir) {
        ChannelPipeline p = channel.pipeline();
        if (p.get(ObjectDecoder.class) != null) {
            p.remove(ObjectDecoder.class);
        }
        if (p.get(CommandInboundHandler.class) != null) {
            p.remove(CommandInboundHandler.class);
        }
        if (p.get(ChunkedWriteHandler.class) == null) {
            p.addLast(new ChunkedWriteHandler());
        }
        if (p.get(FilesWriteHandler.class) == null) {
            p.addLast(new FilesWriteHandler(fileName, fileSize, userDir));
        }
        log.info("Pipeline changed to: " + p);
    }

}
