package ru.online.cloud.server.service.impl;

import ru.online.cloud.server.factory.Factory;
import ru.online.cloud.server.service.CommandDictionaryService;
import ru.online.cloud.server.service.CommandService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandDictionaryServiceImpl implements CommandDictionaryService {

    private final Map<String, CommandService> commandDictionary;

    public CommandDictionaryServiceImpl() {
        commandDictionary = Collections.unmodifiableMap(getCommonDictionary());
    }

    private Map<String, CommandService> getCommonDictionary() {
        List<CommandService> commandServices = Factory.getCommandServices();

        Map<String, CommandService> commandDictionary = new HashMap<>();
        for (CommandService commandService : commandServices) {
            commandDictionary.put(commandService.getCommand(), commandService);
        }

        return commandDictionary;
    }

    @Override
    public String processCommand(String command) {
        String[] commandParts = command.split("\\s");

        if (commandParts.length > 0 && commandDictionary.containsKey(commandParts[0])) {
            return commandDictionary.get(commandParts[0]).processCommand(command);
        }

        return "Error command";
    }

}
