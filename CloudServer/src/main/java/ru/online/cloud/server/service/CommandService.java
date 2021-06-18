package ru.online.cloud.server.service;

import ru.online.domain.Command;

public interface CommandService {

    String processCommand(Command command);

    String getCommand();

}
