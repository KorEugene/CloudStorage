package ru.online.cloud.server.service.impl;

import lombok.extern.log4j.Log4j2;
import ru.online.cloud.server.service.DataBaseProcessService;
import ru.online.cloud.server.util.PropertyUtil;

import java.sql.*;

@Log4j2
public class PostgreSQLService implements DataBaseProcessService {

    private static PostgreSQLService instance;

    private Connection connection;

    private PostgreSQLService() {
    }

    public static PostgreSQLService getInstance() {
        if (instance == null) {
            instance = new PostgreSQLService();
        }
        return instance;
    }

    @Override
    public void start() {
        log.info("Connecting to DB.");
        connect();
        log.info("Connected to DB.");
    }

    @Override
    public void stop() {
        log.info("Disconnecting from DB.");
        disconnect();
        log.info("Disconnected from DB.");
    }

    @Override
    public boolean checkUsername(String username) {
        String checkUsernameQuery = "SELECT * FROM accounts WHERE user_name = ?;";
        try (PreparedStatement ps = connection.prepareStatement(checkUsernameQuery)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException sqlException) {
            log.error(sqlException.getMessage());
        }
        return false;
    }

    @Override
    public int register(String username, String password) {
        int result = 0;
        String registerNewAccount = "INSERT INTO accounts (user_name, user_password) VALUES (?, ?);";
        try (PreparedStatement ps = connection.prepareStatement(registerNewAccount)) {
            ps.setString(1, username);
            ps.setString(2, password);
            result = ps.executeUpdate();
        } catch (SQLException sqlException) {
            log.error(sqlException.getMessage());
        }
        return result;
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        String checkCredentialsQuery = "SELECT * FROM accounts WHERE user_name = ? and user_password = ?;";
        try (PreparedStatement ps = connection.prepareStatement(checkCredentialsQuery)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException sqlException) {
            log.error(sqlException.getMessage());
        }
        return false;
    }

    private void connect() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException exception) {
            log.error(exception.getMessage());
        }
        try {
            connection = DriverManager.getConnection(
                    PropertyUtil.getServerDBConnection(),
                    PropertyUtil.getServerDBLogin(),
                    PropertyUtil.getServerDBPassword()
            );
        } catch (SQLException sqlException) {
            log.error(sqlException.getMessage());
        }
    }

    private void disconnect() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException sqlException) {
            log.error(sqlException.getMessage());
        }
    }
}
