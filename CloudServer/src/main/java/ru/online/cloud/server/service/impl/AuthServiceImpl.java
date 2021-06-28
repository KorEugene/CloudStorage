package ru.online.cloud.server.service.impl;

import ru.online.cloud.server.service.AuthService;

import java.sql.*;

public class AuthServiceImpl implements AuthService {

    private static AuthServiceImpl instance;

    private final String REGISTER_NEW_ACCOUNT = "INSERT INTO accounts (user_name, user_password) VALUES (?, ?);";
    private final String CHECK_PARAMETER_QUERY = "SELECT * FROM accounts WHERE user_name = ? and user_password = ?;";

    private Connection connection;
    private Statement statement;
    private PreparedStatement ps;

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

    @Override
    public int register(String username, String password) {
        int result = 0;
        try {
            ps = connection.prepareStatement(REGISTER_NEW_ACCOUNT);
            ps.setString(1, username);
            ps.setString(2, password);
            result = ps.executeUpdate();
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }
        return result;
    }

    @Override
    public boolean checkLoginPassword(String username, String password) {
        try {
            ps = connection.prepareStatement(CHECK_PARAMETER_QUERY);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }
        return false;
    }

    private void connect() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException exception) {
            System.out.println(exception.getMessage());
        }
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5435/cloud", "postgres", "postgrespass");
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
