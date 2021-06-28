package ru.online.cloud.server.service.impl.command;

import ru.online.cloud.server.factory.Factory;
import ru.online.cloud.server.service.AuthService;
import ru.online.cloud.server.service.CommandService;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RegisterAccountCommand implements CommandService {

    private final AuthService authService = Factory.getAuthService();
    private final String basePath = Factory.getServerService().getProperties().getProperty("cld.directory");

    @Override
    public Command processCommand(Command command) {

        String username = (String) command.getArgs()[0];
        String password = (String) command.getArgs()[1];

        Command result = new Command();

        if (!authService.checkLoginPassword("user_name", username)) {
            int res = authService.register(username, password);
            if (res == 1) {
                result.setCommandName(CommandType.REGISTRATION_SUCCEEDED);
                try {
                    Path path = Paths.get(basePath + File.separator + username);
                    if (Files.notExists(path)) {
                        Files.createDirectory(path);
                    }
                } catch (IOException exception) {
                    System.out.println(exception.getMessage());
                }
            } else {
                result.setCommandName(CommandType.REGISTRATION_FAILED);
            }
        } else {
            result.setCommandName(CommandType.REGISTRATION_FAILED);
        }

        return result;
    }

    @Override
    public CommandType getCommand() {
        return CommandType.REGISTRATION_REQUEST;
    }

}
