package ru.online.cloud.server.service.impl.command;

import ru.online.cloud.server.factory.Factory;
import ru.online.cloud.server.service.AuthService;
import ru.online.cloud.server.service.CommandService;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

import java.io.File;

public class AuthenticateAccountCommand implements CommandService {

    private final AuthService authService = Factory.getAuthService();
    private final String basePath = Factory.getServerService().getProperties().getProperty("cld.directory");

    @Override
    public Command processCommand(Command command) {

        String username = (String) command.getArgs()[0];
        String password = (String) command.getArgs()[1];

        Command result = new Command();

        if (authService.checkParameter("username", username) && authService.checkParameter("password", password)) {
            result.setCommandName(CommandType.AUTH_SUCCEEDED);
            result.setArgs(new Object[]{basePath + File.separator + username});
        } else {
            result.setCommandName(CommandType.AUTH_FAILED);
        }

        return result;
    }

    @Override
    public CommandType getCommand() {
        return CommandType.AUTH_REQUEST;
    }
}
