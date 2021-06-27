package ru.online.cloud.server.service;

import ru.online.domain.command.Command;

public interface CommandDictionaryService {

    Command processCommand(Command command);

//    List<FileInfo> processCommand(Command command);

//    String processCommand(Command command);

}
