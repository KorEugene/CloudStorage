package ru.online.cloud.server;

import org.flywaydb.core.Flyway;
import ru.online.cloud.server.factory.Factory;

public class Main {

    public static void main(String[] args) {
        Flyway flyway = Flyway.configure().dataSource("jdbc:postgresql://localhost:5435/cloud","postgres","postgres").load();
        flyway.migrate();

        Factory.getServerService().startServer();
    }

}
