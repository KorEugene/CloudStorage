package ru.online.cloud.server;

import ru.online.cloud.server.factory.Factory;

public class Main {

    public static void main(String[] args) {
        Factory.getServerService().startServer();
    }

}
