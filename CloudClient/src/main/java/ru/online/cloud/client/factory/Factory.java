package ru.online.cloud.client.factory;

import ru.online.cloud.client.core.NettyClient;
import ru.online.cloud.client.core.service.PipelineProcessor;
import ru.online.cloud.client.core.service.impl.PipelineProcessorImpl;
import ru.online.cloud.client.service.Client;
import ru.online.cloud.client.service.NetworkService;
import ru.online.cloud.client.service.StorageService;
import ru.online.cloud.client.service.impl.LocalStorageService;
import ru.online.cloud.client.service.impl.NettyNetworkService;

public class Factory {

    public static Client getClient() {
        return NettyClient.getInstance();
    }

    public static NetworkService getNetworkService() {
        return NettyNetworkService.getInstance();
    }

    public static PipelineProcessor getPipelineProcessor() {
        return PipelineProcessorImpl.getInstance();
    }

    public static StorageService getStorageService() {
        return LocalStorageService.getInstance();
    }
}
