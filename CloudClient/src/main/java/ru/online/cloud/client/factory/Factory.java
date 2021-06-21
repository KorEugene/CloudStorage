package ru.online.cloud.client.factory;

import ru.online.cloud.client.core.NettyClientService;
import ru.online.cloud.client.service.Callback;
import ru.online.cloud.client.service.ClientService;
import ru.online.cloud.client.service.NetworkService;
import ru.online.cloud.client.service.impl.NettyNetworkService;

public class Factory {

    public static ClientService getClientService(Callback incomingData) {
        return NettyClientService.getInstance(incomingData);
    }

    public static NetworkService getNetworkService(Callback incomingData) {
        return NettyNetworkService.getInstance(incomingData);
    }

}
