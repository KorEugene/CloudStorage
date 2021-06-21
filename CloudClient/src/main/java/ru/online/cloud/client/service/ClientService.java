package ru.online.cloud.client.service;

import ru.online.domain.Command;

public interface ClientService {

    void startClient();

    void stopClient();

    void sendCommand(Command command, Callback callback);
}
