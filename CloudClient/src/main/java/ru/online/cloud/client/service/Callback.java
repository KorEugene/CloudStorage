package ru.online.cloud.client.service;

import ru.online.domain.command.Command;

public interface Callback {

    void callback(Command command);
}
