package ru.online.cloud.client.service;

import ru.online.domain.command.Command;

public interface ClientService {

    void startClient();

    void stopClient();

    void sendCommand(Command command, Callback callback);

    void sendFile(Command command);
}
