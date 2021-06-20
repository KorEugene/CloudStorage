package ru.online.cloud.client.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import ru.online.cloud.client.service.ClientService;
import ru.online.domain.Command;

public class NettyClientService implements ClientService {

    private static final String HOST = "localhost";
    private static final int PORT = 8189;

    private SocketChannel channel;

    private static NettyClientService instance;

    private NettyClientService() {
    }

    public static NettyClientService getInstance() {
        if (instance == null) {
            instance = new NettyClientService();
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
                                        .addLast(new ObjectEncoder());
//                                        .addLast(new ChunkedWriteHandler())
//                                        .addLast(new CommandOutboundHandler());
                            }
                        });
                ChannelFuture future = bootstrap.connect(HOST, PORT).sync();
                System.out.println("Клиент подключился к серверу " + HOST + ":" + PORT);
                future.channel().closeFuture().sync();
                System.out.println("Клиент отключился от сервера " + HOST + ":" + PORT);
            } catch (Exception exception) {
                System.out.println("Клиент упал");
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
    public void sendCommand(Command command) {
        channel.writeAndFlush(command);
    }


}