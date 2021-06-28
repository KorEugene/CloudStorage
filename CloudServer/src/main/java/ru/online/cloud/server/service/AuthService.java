package ru.online.cloud.server.service;

public interface AuthService {

    void start();

    void stop();

    int register(String username, String password);

    boolean checkParameter(String parameter, String value);
}
