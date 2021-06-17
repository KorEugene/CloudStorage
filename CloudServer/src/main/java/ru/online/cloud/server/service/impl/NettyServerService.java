package ru.online.cloud.server.service.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import ru.online.cloud.server.core.CommandInboundHandler;
import ru.online.cloud.server.service.ServerService;

public class NettyServerService implements ServerService {

    private static final int SERVER_PORT = 8189;

    private static NettyServerService instance;

    public static NettyServerService getInstance() {
        if (instance == null) {
            instance = new NettyServerService();
        }
        return instance;
    }

    @Override
    public void startServer() {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
                                .addLast(new ObjectEncoder())
                                .addLast(new CommandInboundHandler());
                    }
                });

        try {
            ChannelFuture future = bootstrap.bind(SERVER_PORT).sync();
            System.out.println("Сервер запущен на порту " + SERVER_PORT);
            future.channel().closeFuture().sync();
            System.out.println("Сервер завершил работу");
        } catch (InterruptedException e) {
            System.out.println("Сервер упал");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }


}
