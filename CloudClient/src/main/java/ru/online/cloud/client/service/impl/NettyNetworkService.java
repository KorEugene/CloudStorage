package ru.online.cloud.client.service.impl;

import ru.online.cloud.client.service.NetworkService;
import ru.online.domain.Command;

public class NettyNetworkService implements NetworkService {

    private static NettyNetworkService instance;

    private NettyNetworkService() {
    }

    public static NetworkService getInstance() {
        if (instance == null) {
            instance = new NettyNetworkService();
        }
        return instance;
    }

    @Override
    public void sendCommand(Command command) {
//        channel.writeAndFlush(command);
    }

    @Override
    public String readCommandResult() {
        return null;
    }

    @Override
    public void closeConnection() {
//            future.channel().close().sync();
    }

}
