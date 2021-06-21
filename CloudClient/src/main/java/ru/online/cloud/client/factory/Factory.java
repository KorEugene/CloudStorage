package ru.online.cloud.client.factory;

import ru.online.cloud.client.core.NettyClientService;
import ru.online.cloud.client.service.ClientService;
import ru.online.cloud.client.service.NetworkService;
import ru.online.cloud.client.service.impl.NettyNetworkService;

public class Factory {

    public static ClientService getClientService() {
        return NettyClientService.getInstance();
    }

    public static NetworkService getNetworkService() {
        return NettyNetworkService.getInstance();
    }

}
