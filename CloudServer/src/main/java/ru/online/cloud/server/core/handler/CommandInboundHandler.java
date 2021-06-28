package ru.online.cloud.server.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import ru.online.cloud.server.factory.Factory;
import ru.online.cloud.server.service.CommandDictionaryService;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

import java.io.File;
import java.io.IOException;

public class CommandInboundHandler extends ChannelInboundHandlerAdapter {

    private CommandDictionaryService dictionaryService;
    private SocketChannel channel;

    public CommandInboundHandler(SocketChannel channel) {
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

        if (command.getCommandName() == CommandType.LS) {
            processLSCommand(ctx, command);
        }

        if (command.getCommandName() == CommandType.UPLOAD) {
            Command result = dictionaryService.processCommand(command);
            ctx.writeAndFlush(result);
            switchToFileUploadPipeline(channel, (String) command.getArgs()[0], (Long) command.getArgs()[1]);
        }

        if (command.getCommandName() == CommandType.DOWNLOAD) {
            Command result = dictionaryService.processCommand(command);
            ctx.writeAndFlush(result);
        }

        if (command.getCommandName() == CommandType.DOWNLOAD_READY) {
            try {
                ChunkedFile chunkedFile = new ChunkedFile(new File(command.getPath()));
                channel.writeAndFlush(chunkedFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processLSCommand(ChannelHandlerContext ctx, Command command) {
        Command result = dictionaryService.processCommand(command);
        ctx.writeAndFlush(result);
    }

    private void switchToFileUploadPipeline(SocketChannel channel, String fileName, long fileSize) {
        ChannelPipeline p = channel.pipeline();
        if (p.get("decoder") != null) {
            p.remove("decoder");
        }
        if (p.get("command") != null) {
            p.remove("command");
        }
        if (p.get("chunkWr") == null) {
            p.addLast("chunkWr", new ChunkedWriteHandler());
        }
        if (p.get("fileWr") == null) {
            p.addLast("fileWr", new FilesWriteHandler(channel, fileName, fileSize));
        }
    }

}
