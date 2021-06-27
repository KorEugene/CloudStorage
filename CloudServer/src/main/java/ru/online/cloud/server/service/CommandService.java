package ru.online.cloud.server.service;

import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

public interface CommandService {

    Command processCommand(Command command);

//    List<FileInfo> processCommand(Command command);

//    String processCommand(Command command);

    CommandType getCommand();

}
