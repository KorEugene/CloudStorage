package ru.online.cloud.client.factory;

import ru.online.cloud.client.service.NetworkService;
import ru.online.cloud.client.service.impl.IONetworkService;

public class Factory {

    public static NetworkService getNetworkService() {
        return IONetworkService.getInstance();
    }

}
