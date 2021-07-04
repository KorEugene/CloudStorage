package ru.online.cloud.server.service.impl;

import lombok.extern.log4j.Log4j2;
import ru.online.cloud.server.service.StorageService;
import ru.online.cloud.server.util.PropertyUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
public class LocalStorageService implements StorageService {

    private static LocalStorageService instance;

    private LocalStorageService() {
    }

    public static LocalStorageService getInstance() {
        if (instance == null) {
            instance = new LocalStorageService();
        }
        return instance;
    }

    @Override
    public void init() {
        String basePath = PropertyUtil.getServerDirectory();
        if (Files.notExists(Paths.get(basePath))) {
            try {
                Files.createDirectories(Paths.get(basePath));
            } catch (IOException exception) {
                log.error(exception.getMessage());
            }
        }
    }

    @Override
    public Path createDirectoryIfNotExists(String path, String directoryName) {
        Path fullPath = null;
        try {
            fullPath = Paths.get(path + File.separator + directoryName);
            if (Files.notExists(fullPath)) {
                Files.createDirectory(fullPath);
            }
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }
        return fullPath;
    }

    @Override
    public boolean checkFileIsExists(File file) {
        return file.exists();
    }

    @Override
    public boolean deleteFile(File file) {
        return file.delete();
    }
}
