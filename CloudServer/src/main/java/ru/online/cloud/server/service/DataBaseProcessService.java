package ru.online.cloud.server.service;

public interface DataBaseProcessService {

    void start();

    void stop();

    boolean checkUsername(String username);

    int register(String username, String password);

    boolean checkCredentials(String username, String password);
}
