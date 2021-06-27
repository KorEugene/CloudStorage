package ru.online.cloud.server.factory;

import ru.online.cloud.server.core.NettyServerService;
import ru.online.cloud.server.service.CommandDictionaryService;
import ru.online.cloud.server.service.CommandService;
import ru.online.cloud.server.service.ServerService;
import ru.online.cloud.server.service.impl.CommandDictionaryServiceImpl;
import ru.online.cloud.server.service.impl.command.DownloadFilesCommand;
import ru.online.cloud.server.service.impl.command.UploadFilesCommand;
import ru.online.cloud.server.service.impl.command.ViewFilesInDirCommand;

import java.util.Arrays;
import java.util.List;

public class Factory {

    public static ServerService getServerService() {
        return NettyServerService.getInstance();
    }

    public static CommandDictionaryService getCommandDirectoryService() {
        return new CommandDictionaryServiceImpl();
    }

    public static List<CommandService> getCommandServices() {
        return Arrays.asList(new ViewFilesInDirCommand(), new UploadFilesCommand(), new DownloadFilesCommand());
    }

}
