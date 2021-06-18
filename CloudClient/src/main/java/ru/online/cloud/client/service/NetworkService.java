package ru.online.cloud.client.service;

import ru.online.domain.Command;

public interface NetworkService {

    void sendCommand(Command command);

    String readCommandResult();

    void closeConnection();
}
