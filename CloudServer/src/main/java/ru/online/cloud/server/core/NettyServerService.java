package ru.online.cloud.server.core;

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
import io.netty.handler.stream.ChunkedWriteHandler;
import ru.online.cloud.server.core.handler.AuthInboundHandler;
import ru.online.cloud.server.core.handler.CommandInboundHandler;
import ru.online.cloud.server.factory.Factory;
import ru.online.cloud.server.service.ServerService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class NettyServerService implements ServerService {

    private static final int SERVER_PORT = 8189;
    private static NettyServerService instance;
    private static Properties properties;

    private NettyServerService() {
        Factory.getAuthService().start();
        loadProperties();
        initialStorage();
    }

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
                    protected void initChannel(SocketChannel channel) {
                        channel.pipeline()
                                .addLast("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
                                .addLast("encoder", new ObjectEncoder())
                                .addLast("auth", new AuthInboundHandler(channel));
//                                .addLast("command", new CommandInboundHandler(channel))
//                                .addLast("chunkWr", new ChunkedWriteHandler());

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

    private void loadProperties() {
        try (InputStream input = NettyServerService.class.getClassLoader().getResourceAsStream("server.properties")) {
            properties = new Properties();
            properties.load(input);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    private void initialStorage() {
        String basePath = getProperties().getProperty("cld.directory");
        if (Files.notExists(Paths.get(basePath))) {
            try {
                Files.createDirectories(Paths.get(basePath));
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

}
