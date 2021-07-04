package ru.online.cloud.client.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.log4j.Log4j2;
import ru.online.cloud.client.core.handler.parameter.FileParameter;
import ru.online.cloud.client.core.service.PipelineProcessor;
import ru.online.cloud.client.service.Callback;
import ru.online.cloud.client.service.StorageService;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

import java.io.*;

@Log4j2
public class FilesWriteHandler extends ChannelInboundHandlerAdapter {

    private final SocketChannel channel;
    private final PipelineProcessor pipelineProcessor;
    private final StorageService storageService;
    private final FileParameter fileParameter;
    private File file;
    private final Callback answer;

    public FilesWriteHandler(SocketChannel channel, PipelineProcessor pipelineProcessor, StorageService storageService, FileParameter fileParameter, Callback answer) {
        this.channel = channel;
        this.pipelineProcessor = pipelineProcessor;
        this.storageService = storageService;
        this.fileParameter = fileParameter;
        this.answer = answer;
        prepareToDownloadFile(fileParameter.getFileName(), fileParameter.getUserDir());
    }

    private void prepareToDownloadFile(String fileName, String userDir) {
        file = new File(userDir + File.separator + fileName);
        if (storageService.checkFileIsExists(file)) {
            if (!storageService.deleteFile(file)) {
                try {
                    throw new IOException();
                } catch (IOException exception) {
                    log.error(exception.getMessage());
                }
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object chunkedFile) {

        ByteBuf byteBuf = (ByteBuf) chunkedFile;

        writeChunk(ctx, byteBuf);

        if (file.length() == fileParameter.getFileSize()) {
            answer.callback(new Command(CommandType.DOWNLOAD_COMPLETE, null, new Object[]{}));
            pipelineProcessor.clear(channel);
            pipelineProcessor.switchToCommand(ctx);
        }
    }

    private void writeChunk(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file, true))) {
            while (byteBuf.isReadable()) {
                os.write(byteBuf.readByte());
            }
        } catch (Exception exception) {
            exceptionCaught(ctx, exception);
        } finally {
            byteBuf.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage());
        pipelineProcessor.clear(channel);
        pipelineProcessor.switchToCommand(ctx);
    }
}
