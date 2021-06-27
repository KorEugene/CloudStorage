package ru.online.cloud.server.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FilesWriteHandler extends ChannelInboundHandlerAdapter {

    private SocketChannel channel;
    private String fileName;
    private long fileSize;
    private File file;

    public FilesWriteHandler(SocketChannel channel, String fileName, long fileSize) {
        this.channel = channel;
        this.fileName = fileName;
        this.fileSize = fileSize;
        System.out.println("file size: " + fileSize);
        checkFileIsExists(fileName);
    }

    private void checkFileIsExists(String fileName) {
        file = new File("C:\\GeekBrainsUniversity\\java\\CloudStorage\\CloudServer\\storage" + File.separator + fileName);
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
            switchToCommandPipeline(channel);
            ctx.writeAndFlush(new Command(CommandType.UPLOAD_COMPLETE, fileName, new Object[]{}));
        }
    }



    private void switchToCommandPipeline(SocketChannel channel) {
        ChannelPipeline p = channel.pipeline();
        if (p.get("chunkWr") != null) {
            p.remove("chunkWr");
        }
        if (p.get("fileWr") != null) {
            p.remove("fileWr");
        }
        if (p.get("decoder") == null) {
            p.addLast("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
        }
        if (p.get("encoder") == null) {
            p.addLast("encoder", new ObjectEncoder());
        }
        if (p.get("command") == null) {
            p.addLast("command", new CommandInboundHandler(channel));
        }
        System.out.println(channel.pipeline());
    }


}
