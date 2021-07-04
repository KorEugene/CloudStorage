package ru.online.cloud.client.service.impl;

import lombok.extern.log4j.Log4j2;
import ru.online.cloud.client.service.StorageService;

import java.io.File;

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
    public boolean checkFileIsExists(File file) {
        return file.exists();
    }

    @Override
    public boolean deleteFile(File file) {
        return file.delete();
    }
}
