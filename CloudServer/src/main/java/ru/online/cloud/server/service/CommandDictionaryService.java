package ru.online.cloud.server.service;

import ru.online.domain.Command;
import ru.online.domain.FileInfo;

import java.util.List;

public interface CommandDictionaryService {

    List<FileInfo> processCommand(Command command);

//    String processCommand(Command command);

}
