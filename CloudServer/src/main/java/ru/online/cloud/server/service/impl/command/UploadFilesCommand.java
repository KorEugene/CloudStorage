package ru.online.cloud.server.service.impl.command;

import ru.online.cloud.server.service.CommandService;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

public class UploadFilesCommand implements CommandService {

    @Override
    public Command processCommand(Command command) {
        return new Command(CommandType.UPLOAD_READY, "", new Object[]{});
    }

    @Override
    public CommandType getCommand() {
        return CommandType.UPLOAD;
    }
}
