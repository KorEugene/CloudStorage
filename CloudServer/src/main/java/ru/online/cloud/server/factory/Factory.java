package ru.online.cloud.server.factory;

import ru.online.cloud.server.core.NettyServer;
import ru.online.cloud.server.service.*;
import ru.online.cloud.server.service.impl.FlywayService;
import ru.online.cloud.server.service.impl.LocalStorageService;
import ru.online.cloud.server.service.impl.PostgreSQLService;
import ru.online.cloud.server.service.impl.CommandDictionaryServiceImpl;
import ru.online.cloud.server.service.impl.command.*;

import java.util.Arrays;
import java.util.List;

public class Factory {

    public static Server getServer() {
        return NettyServer.getInstance();
    }

    public static DataBaseProcessService getDataBaseProcessService() {
        return PostgreSQLService.getInstance();
    }

    public static CommandDictionaryService getCommandDirectoryService() {
        return new CommandDictionaryServiceImpl();
    }

    public static DataBaseMigrationService getDataBaseMigrationService() {
        return FlywayService.getInstance();
    }

    public static StorageService getStorageService() {
        return LocalStorageService.getInstance();
    }

    public static List<CommandService> getCommandServices() {
        return Arrays.asList(new ViewFilesInDirCommand(), new UploadFilesCommand(),
                new DownloadFilesCommand(), new RegisterAccountCommand(),
                new AuthenticateAccountCommand(), new MakeDirectoryCommand());
    }

}
