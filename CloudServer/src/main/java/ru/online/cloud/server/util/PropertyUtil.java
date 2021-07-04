package ru.online.cloud.server.util;

import ru.online.cloud.server.core.NettyServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {

    public static final String SERVER_PROPERTIES_FILE = "server.properties";
    public static final String SERVER_PORT = "server.port";
    public static final String SERVER_DIRECTORY = "server.directory";
    public static final String SERVER_DB_CONNECTION = "server.db_connection";
    public static final String SERVER_DB_LOGIN = "server.db_login";
    public static final String SERVER_DB_PASSWORD = "server.db_password";

    private static Properties properties;

    private PropertyUtil() {
    }

    public static void loadProperties() {
        try (InputStream input = NettyServer.class.getClassLoader().getResourceAsStream(SERVER_PROPERTIES_FILE)) {
            properties = new Properties();
            properties.load(input);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static int getServerPort() {
        return Integer.parseInt(properties.getProperty(SERVER_PORT));
    }

    public static String getServerDirectory() {
        return properties.getProperty(SERVER_DIRECTORY);
    }

    public static String getServerDBConnection() {
        return properties.getProperty(SERVER_DB_CONNECTION);
    }

    public static String getServerDBLogin() {
        return properties.getProperty(SERVER_DB_LOGIN);
    }

    public static String getServerDBPassword() {
        return properties.getProperty(SERVER_DB_PASSWORD);
    }
}
