package ru.online.cloud.server.factory;

import ru.online.cloud.server.core.NettyServerService;
import ru.online.cloud.server.service.AuthService;
import ru.online.cloud.server.service.CommandDictionaryService;
import ru.online.cloud.server.service.CommandService;
import ru.online.cloud.server.service.ServerService;
import ru.online.cloud.server.service.impl.AuthServiceImpl;
import ru.online.cloud.server.service.impl.CommandDictionaryServiceImpl;
import ru.online.cloud.server.service.impl.command.*;

import java.util.Arrays;
import java.util.List;

public class Factory {

    public static ServerService getServerService() {
        return NettyServerService.getInstance();
    }

    public static AuthService getAuthService() {
        return AuthServiceImpl.getInstance();
    }

    public static CommandDictionaryService getCommandDirectoryService() {
        return new CommandDictionaryServiceImpl();
    }

    public static List<CommandService> getCommandServices() {
        return Arrays.asList(new ViewFilesInDirCommand(), new UploadFilesCommand(), new DownloadFilesCommand(), new RegisterAccountCommand(), new AuthenticateAccountCommand());
    }

}
