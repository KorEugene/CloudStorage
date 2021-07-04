package ru.online.cloud.server.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j2;
import ru.online.cloud.server.core.handler.parameter.FileParameter;
import ru.online.cloud.server.core.service.PipelineProcessor;
import ru.online.cloud.server.service.StorageService;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

import java.io.*;

@Log4j2
public class FilesWriteHandler extends ChannelInboundHandlerAdapter {

    private final PipelineProcessor pipelineProcessor;
    private final StorageService storageService;
    private final FileParameter fileParameter;
    private File file;

    public FilesWriteHandler(PipelineProcessor pipelineProcessor, StorageService storageService, FileParameter fileParameter) {
        this.pipelineProcessor = pipelineProcessor;
        this.storageService = storageService;
        this.fileParameter = fileParameter;
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
            ctx.writeAndFlush(new Command(CommandType.UPLOAD_COMPLETE, fileParameter.getFileName(), new Object[]{}));
            pipelineProcessor.clear(ctx);
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
        pipelineProcessor.clear(ctx);
        pipelineProcessor.switchToCommand(ctx);
    }
}
