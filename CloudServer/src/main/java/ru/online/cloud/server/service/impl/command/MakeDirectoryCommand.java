package ru.online.cloud.server.service.impl.command;

import ru.online.cloud.server.service.CommandService;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
Dummy class
 */
public class MakeDirectoryCommand implements CommandService {

    private static final int REQUIREMENT_COUNT_COMMAND_PARTS = 0;

    @Override
    public Command processCommand(Command command) {

        if (command.getArgs().length != REQUIREMENT_COUNT_COMMAND_PARTS) {
            throw new IllegalArgumentException("Command \"" + getCommand() + "\" is not correct");
        }

        Path path = Paths.get(command.getPath());
        if (Files.notExists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
            }
        }

        return new Command(CommandType.MK_DIR, "", new Object[]{});
    }

    @Override
    public CommandType getCommand() {
        return CommandType.MK_DIR;
    }

    //TODO: client part
}
