package ru.online.cloud.server.service.impl.command;

import ru.online.cloud.server.factory.Factory;
import ru.online.cloud.server.service.CommandService;
import ru.online.cloud.server.service.DataBaseProcessService;
import ru.online.cloud.server.util.CommandUtil;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

public class RegisterAccountCommand implements CommandService {

    private static final int REQUIREMENT_COUNT_COMMAND_PARTS = 2;
    private final DataBaseProcessService dataBaseProcessService;

    public RegisterAccountCommand() {
        dataBaseProcessService = Factory.getDataBaseProcessService();
    }

    @Override
    public Command processCommand(Command command) {

        CommandUtil.checkCommandArgsCount(command, REQUIREMENT_COUNT_COMMAND_PARTS);

        String username = (String) command.getArgs()[0];
        String password = (String) command.getArgs()[1];

        Command result = new Command();

        if (!dataBaseProcessService.checkUsername(username)) {
            if (dataBaseProcessService.register(username, password) != 0) {
                result.setCommandName(CommandType.REGISTRATION_SUCCEEDED);
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
