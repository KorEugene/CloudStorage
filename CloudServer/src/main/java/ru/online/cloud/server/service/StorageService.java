package ru.online.cloud.server.service;

import java.io.File;
import java.nio.file.Path;

public interface StorageService {

    void init();

    Path createDirectoryIfNotExists(String path, String directoryName);

    boolean checkFileIsExists(File file);

    boolean deleteFile(File file);
}
