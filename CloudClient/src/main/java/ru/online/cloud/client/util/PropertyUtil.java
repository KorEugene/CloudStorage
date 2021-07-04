package ru.online.cloud.client.util;

import ru.online.cloud.client.core.NettyClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {

    public static final String CLIENT_PROPERTIES_FILE = "client.properties";
    public static final String CLIENT_DEFAULT_DIRECTORY = "client.default.directory";
    public static final String CLIENT_SERVER_HOST = "client.server.host";
    public static final String CLIENT_SERVER_PORT = "client.server.port";

    private static Properties properties;

    private PropertyUtil() {
    }

    public static void loadProperties() {
        try (InputStream input = NettyClient.class.getClassLoader().getResourceAsStream(CLIENT_PROPERTIES_FILE)) {
            properties = new Properties();
            properties.load(input);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static String getDefaultUserDirectory() {
        return properties.getProperty(CLIENT_DEFAULT_DIRECTORY);
    }

    public static String getServerHost() {
        return properties.getProperty(CLIENT_SERVER_HOST);
    }

    public static String getServerPort() {
        return properties.getProperty(CLIENT_SERVER_PORT);
    }
}
