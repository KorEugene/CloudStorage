package ru.online.cloud.client.service;

import java.io.File;

public interface StorageService {

    boolean checkFileIsExists(File file);

    boolean deleteFile(File file);
}
