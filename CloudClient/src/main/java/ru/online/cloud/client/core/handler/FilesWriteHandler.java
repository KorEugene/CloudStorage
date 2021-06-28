package ru.online.cloud.client.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import ru.online.cloud.client.service.Callback;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FilesWriteHandler extends ChannelInboundHandlerAdapter {

    private SocketChannel channel;
    private String fileName;
    private String path;
    private long fileSize;
    private File file;
    private Callback answer;

    public FilesWriteHandler(SocketChannel channel, String fileName, long fileSize, String path, Callback answer) {
        this.channel = channel;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.path = path;
        System.out.println("file size: " + fileSize);
        checkFileIsExists(path, fileName);
        this.answer = answer;
    }

    private void checkFileIsExists(String path, String fileName) {
        file = new File(path + File.separator + fileName);
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
        }

        byteBuf.release();

        if (fileSize == 0) {
            switchToCommandPipeline(channel, answer);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(cause.getMessage());
        switchToCommandPipeline(channel, answer);
    }

    private void switchToCommandPipeline(SocketChannel channel, Callback callback) {
        ChannelPipeline p = channel.pipeline();
        if (p.get("chunkWr") != null) {
            p.remove("chunkWr");
        }
        if (p.get("fileWr") != null) {
            p.remove("fileWr");
        }
        if (p.get("decoder") == null) {
            p.addBefore("encoder","decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
        }
        if (p.get("encoder") == null) {
            p.addLast("encoder", new ObjectEncoder());
        }
        if (p.get("dataInHandler") == null) {
            p.addLast("dataInHandler", new DataInboundHandler());
        }
        if (p.get("chunkWr") == null) {
            p.addLast("chunkWr", new ChunkedWriteHandler());
        }
        System.out.println(channel.pipeline());
        callback.callback(new Command(CommandType.DOWNLOAD_COMPLETE, null, new Object[]{}));
    }

}
