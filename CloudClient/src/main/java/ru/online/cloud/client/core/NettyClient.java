package ru.online.cloud.client.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.log4j.Log4j2;
import ru.online.cloud.client.core.handler.DataInboundHandler;
import ru.online.cloud.client.core.handler.parameter.FileParameter;
import ru.online.cloud.client.core.service.PipelineProcessor;
import ru.online.cloud.client.factory.Factory;
import ru.online.cloud.client.service.Callback;
import ru.online.cloud.client.service.Client;
import ru.online.cloud.client.util.PropertyUtil;
import ru.online.domain.command.Command;

import java.io.File;
import java.io.IOException;

@Log4j2
public class NettyClient implements Client {

    private static final java.lang.String HOST = PropertyUtil.getServerHost();
    private static final int PORT = Integer.parseInt(PropertyUtil.getServerPort());

    private final PipelineProcessor pipelineProcessor;
    private SocketChannel channel;

    private static NettyClient instance;

    private NettyClient(PipelineProcessor pipelineProcessor) {
        this.pipelineProcessor = pipelineProcessor;
    }

    public static NettyClient getInstance() {
        if (instance == null) {
            instance = new NettyClient(Factory.getPipelineProcessor());
        }
        return instance;
    }

    @Override
    public void startClient() {
        Thread networkThread = new Thread(() -> {
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) {
                                channel = socketChannel;
                                socketChannel.pipeline()
                                        .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
                                        .addLast(new ObjectEncoder())
                                        .addLast(new DataInboundHandler())
                                        .addLast(new ChunkedWriteHandler());
                            }
                        });
                ChannelFuture future = bootstrap.connect(HOST, PORT).sync();
                log.info("Client connected to server " + HOST + ":" + PORT);
                future.channel().closeFuture().sync();
                log.info("Client disconnected from server " + HOST + ":" + PORT);
            } catch (Exception exception) {
                log.error("Client crashed");
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
        networkThread.start();
    }

    @Override
    public void stopClient() {
        try {
            if (channel.isActive()) {
                channel.close().sync();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendCommand(Command command, Callback callback) {

        ChannelPipeline p = channel.pipeline();
        p.get(DataInboundHandler.class).setIncomingData(callback);

        channel.writeAndFlush(command);
    }

    @Override
    public void downloadFile(Command command, Callback callback) {

        channel.writeAndFlush(command);
        pipelineProcessor.clear(channel);
        pipelineProcessor.switchToFileUpload(channel, new FileParameter((String) command.getArgs()[0], (long) command.getArgs()[1], (String) command.getArgs()[2]), callback);
    }

    @Override
    public void sendFile(Command command, Callback callback) {
        try {
            channel.writeAndFlush(new ChunkedFile(new File(command.getPath())));
            ChannelPipeline p = channel.pipeline();
            p.get(DataInboundHandler.class).setIncomingData(callback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
