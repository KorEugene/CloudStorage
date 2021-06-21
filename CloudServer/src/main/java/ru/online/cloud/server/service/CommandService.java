package ru.online.cloud.server.service;

import ru.online.domain.Command;
import ru.online.domain.CommandType;
import ru.online.domain.FileInfo;

import java.util.List;

public interface CommandService {

    List<FileInfo> processCommand(Command command);

//    String processCommand(Command command);

    CommandType getCommand();

}
