package ru.online.cloud.server.service.impl;

import ru.online.domain.Command;
import ru.online.cloud.server.factory.Factory;
import ru.online.cloud.server.service.CommandDictionaryService;
import ru.online.cloud.server.service.CommandService;
import ru.online.domain.CommandType;
import ru.online.domain.FileInfo;

import java.util.*;

public class CommandDictionaryServiceImpl implements CommandDictionaryService {

    private final Map<CommandType, CommandService> commandDictionary;

    public CommandDictionaryServiceImpl() {
        commandDictionary = Collections.unmodifiableMap(getCommonDictionary());
    }

    private Map<CommandType, CommandService> getCommonDictionary() {
        List<CommandService> commandServices = Factory.getCommandServices();

        Map<CommandType, CommandService> commandDictionary = new HashMap<>();
        for (CommandService commandService : commandServices) {
            commandDictionary.put(commandService.getCommand(), commandService);
        }

        return commandDictionary;
    }

    @Override
    public List<FileInfo> processCommand(Command command) {
//    public String processCommand(Command command) {
        if (commandDictionary.containsKey(command.getCommandName())) {
            return commandDictionary.get(command.getCommandName()).processCommand(command);
        }

//        return "Error command";
        return new ArrayList<>();
    }

}
