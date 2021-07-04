package ru.online.cloud.server.util;

import ru.online.domain.command.Command;

public class CommandUtil {

    private CommandUtil() {
    }

    public static void checkCommandArgsCount(Command command, int requiredNumberOfArguments) {
        if (command.getArgs().length != requiredNumberOfArguments) {
            throw new IllegalArgumentException("Command \"" + command.getCommandName() + "\" is not correct");
        }
    }
}
