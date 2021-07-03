package ru.online.cloud.server;

import ru.online.cloud.server.factory.Factory;
import ru.online.cloud.server.util.PropertyUtil;

public class Main {

    public static void main(String[] args) {
        PropertyUtil.loadProperties();

        Factory.getDataBaseMigrationService().migrate();

        Factory.getStorageService().init();

        Factory.getServer().startServer();
    }
}
