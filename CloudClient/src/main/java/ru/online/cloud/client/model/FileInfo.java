package ru.online.cloud.client.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class FileInfo {

    private String fileName;
    private FileType type;
    private long size;
    private LocalDateTime lastModified;

    public FileInfo(Path path) {
        try {
            this.fileName = path.getFileName().toString();
            this.size = Files.size(path);
            this.type = Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE;
            if (this.type == FileType.DIRECTORY) {
                this.size = -1L;
            }
            this.lastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), OffsetDateTime.now().getOffset());
        } catch (IOException e) {
            throw new RuntimeException("Unable to create file info from path");
        }
    }

    public String getFileName() {
        return fileName;
    }

    public FileType getType() {
        return type;
    }

    public long getSize() {
        return size;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }
}
