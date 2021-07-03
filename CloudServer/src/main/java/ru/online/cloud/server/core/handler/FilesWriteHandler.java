package ru.online.cloud.server.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.log4j.Log4j2;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@Log4j2
public class FilesWriteHandler extends ChannelInboundHandlerAdapter {

    private String fileName;
    private long fileSize;
    private File file;
    private String userDir;

    public FilesWriteHandler(String fileName, long fileSize, String userDir) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.userDir = userDir;
        checkFileIsExists(fileName);
    }

    private void checkFileIsExists(String fileName) {
        file = new File(userDir + File.separator + fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object chunkedFile) {

        ByteBuf byteBuf = (ByteBuf) chunkedFile;

        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file, true))) {
            while (byteBuf.isReadable()) {
                os.write(byteBuf.readByte());
                fileSize--;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            byteBuf.release();
        }

//        byteBuf.release();

        if (fileSize == 0) {
            switchToCommandPipeline(ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage());
        switchToCommandPipeline(ctx.channel());
    }

    private void switchToCommandPipeline(Channel channel) {
        ChannelPipeline p = channel.pipeline();
        if (p.get(ObjectEncoder.class) != null) {
            p.remove(ObjectEncoder.class);
        }
        if (p.get(ChunkedWriteHandler.class) != null) {
            p.remove(ChunkedWriteHandler.class);
        }
        if (p.get(FilesWriteHandler.class) != null) {
            p.remove(FilesWriteHandler.class);
        }
        if (p.get(ObjectDecoder.class) == null) {
            p.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
        }
        if (p.get(ObjectEncoder.class) == null) {
            p.addLast(new ObjectEncoder());
        }
        if (p.get(CommandInboundHandler.class) == null) {
            p.addLast(new CommandInboundHandler());
        }
        if (p.get(ChunkedWriteHandler.class) == null) {
            p.addLast(new ChunkedWriteHandler());
        }
        log.info("Pipeline changed to: " + p);
        channel.writeAndFlush(new Command(CommandType.UPLOAD_COMPLETE, fileName, new Object[]{}));
    }
}
