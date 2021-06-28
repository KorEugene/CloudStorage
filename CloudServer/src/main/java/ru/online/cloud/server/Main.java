package ru.online.cloud.server;

import org.flywaydb.core.Flyway;
import ru.online.cloud.server.core.NettyServerService;
import ru.online.cloud.server.factory.Factory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {

    private static Properties properties;

    public static void main(String[] args) {
        loadProperties();
        Flyway flyway = Flyway.configure().dataSource(properties.getProperty("cld.db_connection"),
                properties.getProperty("cld.db_login"),properties.getProperty("cld.db_password")).load();
        flyway.migrate();

        Factory.getServerService().startServer();
    }

    private static void loadProperties() {
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("server.properties")) {
            properties = new Properties();
            properties.load(input);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
