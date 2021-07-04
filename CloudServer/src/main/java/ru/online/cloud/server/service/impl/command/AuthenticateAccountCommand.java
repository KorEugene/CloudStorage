package ru.online.cloud.server.service.impl.command;

import ru.online.cloud.server.factory.Factory;
import ru.online.cloud.server.service.CommandService;
import ru.online.cloud.server.service.DataBaseProcessService;
import ru.online.cloud.server.service.StorageService;
import ru.online.cloud.server.util.PropertyUtil;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

import java.nio.file.Path;

public class AuthenticateAccountCommand implements CommandService {

    private static final int REQUIREMENT_COUNT_COMMAND_PARTS = 2;
    private final DataBaseProcessService dataBaseProcessService;
    private final StorageService storageService;

    public AuthenticateAccountCommand() {
        dataBaseProcessService = Factory.getDataBaseProcessService();
        storageService = Factory.getStorageService();
    }

    @Override
    public Command processCommand(Command command) {

        if (command.getArgs().length != REQUIREMENT_COUNT_COMMAND_PARTS) {
            throw new IllegalArgumentException("Command \"" + getCommand() + "\" is not correct");
        }

        String username = (String) command.getArgs()[0];
        String password = (String) command.getArgs()[1];

        Command result = new Command();

        if (dataBaseProcessService.checkCredentials(username, password)) {
            Path userDirectory = storageService.createDirectoryIfNotExists(PropertyUtil.getServerDirectory(), username);
            if (userDirectory != null) {
                result.setCommandName(CommandType.AUTH_SUCCEEDED);
                result.setArgs(new Object[]{userDirectory.toString()});
            }
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
