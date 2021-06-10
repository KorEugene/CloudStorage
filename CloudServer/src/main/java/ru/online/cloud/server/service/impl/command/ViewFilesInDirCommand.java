package ru.online.cloud.server.service.impl.command;

import ru.online.cloud.server.service.CommandService;

import java.io.File;

public class ViewFilesInDirCommand implements CommandService {

    @Override
    public String processCommand(String command) {
        final int requirementCountCommandParts = 2;

        String[] actualCommandParts = command.split("\\s");
        if (actualCommandParts.length != requirementCountCommandParts) {
            throw new IllegalArgumentException("Command \"" + getCommand() + "\" is not correct");
        }

        return process(actualCommandParts[1]);
    }

    private String process(String dirPath) {
        File directory = new File(dirPath);

        if (!directory.exists()) {
            return "Directory is not exists";
        }

        StringBuilder builder = new StringBuilder();
        for (File childFile : directory.listFiles()) {
            String typeFile = getTypeFile(childFile);
            builder.append(childFile.getName()).append(" | ").append(typeFile).append(System.lineSeparator());
        }

        return builder.toString();
    }

    private String getTypeFile(File childFile) {
        return childFile.isDirectory() ? "DIR" : "FILE";
    }

    @Override
    public String getCommand() {
        return "ls";
    }

}
