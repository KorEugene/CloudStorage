package ru.online.cloud.server.service;

import java.nio.file.Path;

public interface StorageService {

    void init();

    Path createDirectoryIfNotExists(String path, String directoryName);
}
