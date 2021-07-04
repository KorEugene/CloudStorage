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
import lombok.extern.log4j.Log4j2;
import ru.online.cloud.server.core.handler.AuthInboundHandler;
import ru.online.cloud.server.factory.Factory;
import ru.online.cloud.server.service.Server;
import ru.online.cloud.server.util.PropertyUtil;

@Log4j2
public class NettyServer implements Server {

    public static final int IN_CONNECTION_ACCEPT_GROUP_COUNT = 1;
    private static final int SERVER_PORT = PropertyUtil.getServerPort();

    private static NettyServer instance;

    private NettyServer() {
    }

    public static NettyServer getInstance() {
        if (instance == null) {
            instance = new NettyServer();
        }
        return instance;
    }

    @Override
    public void startServer() {

        EventLoopGroup bossGroup = new NioEventLoopGroup(IN_CONNECTION_ACCEPT_GROUP_COUNT);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        channel.pipeline()
                                .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
                                .addLast(new ObjectEncoder())
                                .addLast(new AuthInboundHandler(Factory.getDictionaryService(Factory.getAuthServices()),
                                        Factory.getPipelineProcessor()));
                    }
                });

        try {
            ChannelFuture future = bootstrap.bind(SERVER_PORT).sync();
            log.info("Server is running on the port " + SERVER_PORT + ".");
            future.channel().closeFuture().sync();
            log.info("Server shutdown.");
        } catch (InterruptedException e) {
            log.error("Server crashed");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
