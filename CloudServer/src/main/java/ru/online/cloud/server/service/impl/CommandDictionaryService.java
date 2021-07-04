package ru.online.cloud.server.service.impl;

import ru.online.cloud.server.service.CommandService;
import ru.online.cloud.server.service.DictionaryService;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandDictionaryService implements DictionaryService {

    private final Map<CommandType, CommandService> commandDictionary;
    private final List<CommandService> services;

    public CommandDictionaryService(List<CommandService> services) {
        this.services = services;
        this.commandDictionary = Collections.unmodifiableMap(getCommonDictionary());
    }

    @Override
    public Command processCommand(Command command) {
        if (commandDictionary.containsKey(command.getCommandName())) {
            return commandDictionary.get(command.getCommandName()).processCommand(command);
        }

        return new Command();
    }

    private Map<CommandType, CommandService> getCommonDictionary() {

        Map<CommandType, CommandService> commandDictionary = new HashMap<>();
        for (CommandService commandService : services) {
            commandDictionary.put(commandService.getCommand(), commandService);
        }

        return commandDictionary;
    }

}
