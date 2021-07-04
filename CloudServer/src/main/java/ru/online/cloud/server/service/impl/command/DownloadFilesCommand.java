package ru.online.cloud.server.service.impl.command;

import ru.online.cloud.server.service.CommandService;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

public class DownloadFilesCommand implements CommandService {

    private static final int REQUIREMENT_COUNT_COMMAND_PARTS = 0;

    @Override
    public Command processCommand(Command command) {

        if (command.getArgs().length != REQUIREMENT_COUNT_COMMAND_PARTS) {
            throw new IllegalArgumentException("Command \"" + getCommand() + "\" is not correct");
        }

        return new Command(CommandType.DOWNLOAD_READY, "", new Object[]{});
    }

    @Override
    public CommandType getCommand() {
        return CommandType.DOWNLOAD;
    }
}
