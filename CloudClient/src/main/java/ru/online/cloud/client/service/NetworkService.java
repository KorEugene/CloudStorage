package ru.online.cloud.client.service;

import ru.online.domain.command.Command;


public interface NetworkService {

    void sendCommand(Command command, Callback callback);

    void sendFile(Command command, Callback callback);

    void openConnection();

    void closeConnection();
}
