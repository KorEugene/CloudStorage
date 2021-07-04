package ru.online.cloud.server.factory;

import ru.online.cloud.server.core.NettyServer;
import ru.online.cloud.server.core.service.PipelineProcessor;
import ru.online.cloud.server.core.service.impl.PipelineProcessorImpl;
import ru.online.cloud.server.service.*;
import ru.online.cloud.server.service.impl.CommandDictionaryService;
import ru.online.cloud.server.service.impl.FlywayService;
import ru.online.cloud.server.service.impl.LocalStorageService;
import ru.online.cloud.server.service.impl.PostgreSQLService;
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

    public static PipelineProcessor getPipelineProcessor() {
        return PipelineProcessorImpl.getInstance();
    }

    public static DictionaryService getDictionaryService(List<CommandService> services) {
        return new CommandDictionaryService(services);
    }

    public static DataBaseMigrationService getDataBaseMigrationService() {
        return FlywayService.getInstance();
    }

    public static StorageService getStorageService() {
        return LocalStorageService.getInstance();
    }

    public static List<CommandService> getAuthServices() {
        return Arrays.asList(new RegisterAccountCommand(), new AuthenticateAccountCommand());
    }

    public static List<CommandService> getCommandServices() {
        return Arrays.asList(new ViewFilesInDirCommand(), new UploadFilesCommand(),
                new DownloadFilesCommand(), new MakeDirectoryCommand());
    }

}
