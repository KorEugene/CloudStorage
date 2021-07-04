package ru.online.cloud.client.service.impl;

import ru.online.cloud.client.factory.Factory;
import ru.online.cloud.client.service.Callback;
import ru.online.cloud.client.service.Client;
import ru.online.cloud.client.service.NetworkService;
import ru.online.domain.command.Command;

public class NettyNetworkService implements NetworkService {

    private final Client client;

    private static NettyNetworkService instance;

    private NettyNetworkService() {
        this.client = Factory.getClient();
    }

    public static NetworkService getInstance() {
        if (instance == null) {
            instance = new NettyNetworkService();
        }
        return instance;
    }

    @Override
    public void sendCommand(Command command, Callback callback) {
        client.sendCommand(command, callback);
    }

    @Override
    public void sendDownloadCommand(Command command, Callback callback) {
        client.downloadFile(command, callback);
    }

    @Override
    public void sendFile(Command command, Callback callback) {
        client.sendFile(command, callback);
    }

    @Override
    public void openConnection() {
        client.startClient();
    }

    @Override
    public void closeConnection() {
        client.stopClient();
    }

}
