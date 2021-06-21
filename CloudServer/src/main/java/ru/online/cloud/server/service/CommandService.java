package ru.online.cloud.server.service;

import ru.online.domain.Command;

public interface CommandService {

    java.lang.String processCommand(Command command);

    java.lang.String getCommand();

}
