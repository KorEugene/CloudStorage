package ru.online.cloud.server.service.impl.command;

import ru.online.cloud.server.service.CommandService;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;
import ru.online.domain.FileInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ViewFilesInDirCommand implements CommandService {

    @Override
    public Command processCommand(Command command) {

        Command result = new Command();
        result.setCommandName(command.getCommandName());
        result.setPath(command.getPath());
        result.setArgs(new List[]{process(command.getPath())});

        return result;
    }

    private List<FileInfo> process(String dirPath) {
        List<FileInfo> files = new ArrayList<>();
        try {
            files = Files.list(Paths.get(dirPath)).map(FileInfo::new).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    @Override
    public CommandType getCommand() {
        return CommandType.LS;
    }
}
