package ru.online.cloud.client.service.impl;

import ru.online.cloud.client.factory.Factory;
import ru.online.cloud.client.service.Callback;
import ru.online.cloud.client.service.ClientService;
import ru.online.cloud.client.service.NetworkService;
import ru.online.domain.Command;

public class NettyNetworkService implements NetworkService {

    private ClientService clientService;

    private static NettyNetworkService instance;

    private NettyNetworkService(Callback incomingData) {
        this.clientService = Factory.getClientService(incomingData);
    }

    public static NetworkService getInstance(Callback incomingData) {
        if (instance == null) {
            instance = new NettyNetworkService(incomingData);
        }
        return instance;
    }

    @Override
    public void sendCommand(Command command) {
        clientService.sendCommand(command);
    }

    @Override
    public java.lang.String readCommandResult() {
        return null;
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
