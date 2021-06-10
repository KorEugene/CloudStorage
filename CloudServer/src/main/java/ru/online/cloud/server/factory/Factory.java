package ru.online.cloud.server.factory;

import ru.online.cloud.server.service.ClientService;
import ru.online.cloud.server.service.CommandDictionaryService;
import ru.online.cloud.server.service.CommandService;
import ru.online.cloud.server.service.ServerService;
import ru.online.cloud.server.service.impl.CommandDictionaryServiceImpl;
import ru.online.cloud.server.service.impl.IOClientService;
import ru.online.cloud.server.service.impl.SocketServerService;
import ru.online.cloud.server.service.impl.command.ViewFilesInDirCommand;

import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class Factory {

    public static ServerService getServerService() {
        return SocketServerService.getInstance();
    }

    public static ClientService getClientService(Socket socket) {
        return new IOClientService(socket);
    }

    public static CommandDictionaryService getCommandDirectoryService() {
        return new CommandDictionaryServiceImpl();
    }

    public static List<CommandService> getCommandServices() {
        return Arrays.asList(new ViewFilesInDirCommand());
    }

}
