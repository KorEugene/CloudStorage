package ru.online.cloud.server.service.impl.command;

import ru.online.domain.Command;
import ru.online.cloud.server.service.CommandService;
import ru.online.domain.CommandType;
import ru.online.domain.FileInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ViewFilesInDirCommand implements CommandService {

    @Override
    public List<FileInfo> processCommand(Command command) {
//    public String processCommand(Command command) {
        final int requirementCountArgs = 1;

        if (command.getArgs().length != requirementCountArgs) {
            throw new IllegalArgumentException("Command \"" + getCommand() + "\" is not correct");
        }

        return process((String) command.getArgs()[0]);
    }

    private List<FileInfo> process(String dirPath) {
//    private String process(String dirPath) {
//        File directory = new File(dirPath);
//
//        if (!directory.exists()) {
//            return "Directory is not exists";
//        }

//        StringBuilder builder = new StringBuilder();
//        for (File childFile : directory.listFiles()) {
//            String typeFile = getTypeFile(childFile);
//            builder.append(childFile.getName()).append(" | ").append(typeFile).append(System.lineSeparator());
//        }

        List<FileInfo> files = new ArrayList<>();
        try {
            files = Files.list(Paths.get(dirPath)).map(FileInfo::new).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        files.sort(Comparator.comparing(FileInfo::getType));
//        return builder.toString();
        return files;
    }

//    private String getTypeFile(File childFile) {
//        return childFile.isDirectory() ? "DIR" : "FILE";
//    }

    @Override
    public CommandType getCommand() {
        return CommandType.LS;
    }

}
