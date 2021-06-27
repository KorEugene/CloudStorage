package ru.online.cloud.client.service.impl;

import ru.online.cloud.client.factory.Factory;
import ru.online.cloud.client.service.Callback;
import ru.online.cloud.client.service.ClientService;
import ru.online.cloud.client.service.NetworkService;
import ru.online.domain.command.Command;

public class NettyNetworkService implements NetworkService {

    private ClientService clientService;

    private static NettyNetworkService instance;

    private NettyNetworkService() {
        this.clientService = Factory.getClientService();
    }

    public static NetworkService getInstance() {
        if (instance == null) {
            instance = new NettyNetworkService();
        }
        return instance;
    }

    @Override
    public void sendCommand(Command command, Callback callback) {
        clientService.sendCommand(command, callback);
    }

    @Override
    public void sendDownloadCommand(Command command, Callback callback) {
        clientService.downloadFile(command, callback);
    }

    @Override
    public void sendFile(Command command, Callback callback) {
        clientService.sendFile(command, callback);
    }

    @Override
    public void openConnection() {
        clientService.startClient();
    }

    @Override
    public void closeConnection() {
        clientService.stopClient();
    }

}
