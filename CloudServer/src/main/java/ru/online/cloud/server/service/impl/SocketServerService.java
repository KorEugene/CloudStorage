package ru.online.cloud.server.service.impl;

import ru.online.cloud.server.factory.Factory;
import ru.online.cloud.server.service.ServerService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServerService implements ServerService {

    private static final int SERVER_PORT = 8189;

    private static SocketServerService instance;

    public static SocketServerService getInstance() {
        if (instance == null) {
            instance = new SocketServerService();
        }

        return instance;
    }

    @Override
    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Сервер запущен на порту " + SERVER_PORT);

            while(true) {
                Socket clientSocket = serverSocket.accept();
                Factory.getClientService(clientSocket).startIOProcess();
                System.out.println("Подключился новый клиент");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Сервер завершил работу");
    }

}
