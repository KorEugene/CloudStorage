package ru.online.cloud.server.service;

import ru.online.domain.Command;

public interface CommandDictionaryService {

    String processCommand(Command command);

}
