package ru.online.cloud.server.service;

import ru.online.domain.command.Command;

public interface DictionaryService {

    Command processCommand(Command command);
}
