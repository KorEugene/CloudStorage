package ru.online.cloud.server.service.impl;

import ru.online.cloud.server.core.NettyServerService;
import ru.online.cloud.server.service.AuthService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class AuthServiceImpl implements AuthService {

    private static AuthServiceImpl instance;

    private Connection connection;
    private Statement statement;

    private AuthServiceImpl() {
    }

    public static AuthServiceImpl getInstance() {
        if (instance == null) {
            instance = new AuthServiceImpl();
        }
        return instance;
    }

    @Override
    public void start() {
        System.out.println("Connecting to DB.");

        connect();
        System.out.println("Connected to DB. Auth started.");
    }

    @Override
    public void stop() {
        System.out.println("Disconnecting from DB.");
        disconnect();
        System.out.println("Disconnected from DB. Auth stopped.");
    }

    private void connect() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException exception) {
            System.out.println(exception.getMessage());
        }
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5435/cloud");
            statement = connection.createStatement();
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void disconnect() {
        try {
            if (statement != null) statement.close();
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
        try {
            if (connection != null) connection.close();
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

}
