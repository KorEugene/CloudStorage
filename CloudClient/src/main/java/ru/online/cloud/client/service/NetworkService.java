package ru.online.cloud.client.service;

public interface NetworkService {

    void sendCommand(String command);

    String readCommandResult();

    void closeConnection();

}
