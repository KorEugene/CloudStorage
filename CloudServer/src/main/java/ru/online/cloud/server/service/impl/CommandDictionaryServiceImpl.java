package ru.online.cloud.server.service.impl;

import ru.online.cloud.server.factory.Factory;
import ru.online.cloud.server.service.CommandDictionaryService;
import ru.online.cloud.server.service.CommandService;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandDictionaryServiceImpl implements CommandDictionaryService {

    private final Map<CommandType, CommandService> commandDictionary;

    public CommandDictionaryServiceImpl() {
        commandDictionary = Collections.unmodifiableMap(getCommonDictionary());
    }

    @Override
    public Command processCommand(Command command) {
        if (commandDictionary.containsKey(command.getCommandName())) {
            return commandDictionary.get(command.getCommandName()).processCommand(command);
        }

        return new Command();
    }

    private Map<CommandType, CommandService> getCommonDictionary() {
        List<CommandService> commandServices = Factory.getCommandServices();

        Map<CommandType, CommandService> commandDictionary = new HashMap<>();
        for (CommandService commandService : commandServices) {
            commandDictionary.put(commandService.getCommand(), commandService);
        }

        return commandDictionary;
    }

}
