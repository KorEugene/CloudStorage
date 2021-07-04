package ru.online.cloud.client.core.handler.parameter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileParameter {

    private final String fileName;
    private final long fileSize;
    private final String userDir;
}
