package ru.online.cloud.client.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.online.cloud.client.factory.Factory;
import ru.online.domain.FileInfo;
import ru.online.cloud.client.service.NetworkService;
import ru.online.domain.Command;
import ru.online.domain.CommandType;
import ru.online.domain.FileType;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    public TextField commandTextField;
    public TextArea commandResultTextArea;

    public NetworkService networkService;

    private String cloudPath;
    private Properties properties;

    @FXML
    public Button btnListDirs;

    @FXML
    private Button btnConnect;
    @FXML
    private Button btnDisconnect;
    @FXML
    private TextField localPathField;
    @FXML
    private TextField cloudPathField;
    @FXML
    private ComboBox<String> disksBox;
    @FXML
    private TableView<FileInfo> localFiles;
    @FXML
    private TableView<FileInfo> cloudFiles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadProperties();
        btnDisconnect.setDisable(true);

        initLocalFilePanel();
        initCloudFilePanel();


    }

    private void loadProperties() {
        try (InputStream input = MainController.class.getClassLoader().getResourceAsStream("client.properties")) {
            properties = new Properties();
            properties.load(input);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void createCommandResultHandler() {
//        new Thread(() -> {
//            while (true) {
//                String resultCommand = networkService.readCommandResult();
//                Platform.runLater(() -> commandResultTextArea.appendText(resultCommand + System.lineSeparator()));
//            }
//        }).start();
    }

    public void sendCommand(ActionEvent actionEvent) {
//        String[] textCommand = commandTextField.getText().trim().split("\\s");
//        if (textCommand.length > 1) {
//            String[] commandArgs = Arrays.copyOfRange(textCommand, 1, textCommand.length);
//            networkService.sendCommand(new Command(textCommand[0], commandArgs));
//            commandTextField.clear();
//        }
    }

    public void shutdown() {
        disconnectFromServer();
        Platform.exit();
    }

    public void btnCloseApp() {
        shutdown();
    }

    public void btnLocalPathUp() {
        Path upperPath = Paths.get(localPathField.getText()).getParent();
        if (upperPath != null) {
            updateList(upperPath, localFiles, localPathField);
        }
    }

    public void btnCloudPathUp(ActionEvent actionEvent) {
        //TODO
        Path upperPath = Paths.get(cloudPathField.getText()).getParent();
        if (upperPath != null) {
//            updateList(upperPath, cloudFiles, cloudPathField);
            updateCloudList(upperPath, cloudFiles, cloudPathField);
        }
    }

    private void initLocalFilePanel() {
        initTableView(localFiles, localPathField);
        initLocalDisks();
        updateList(Paths.get(properties.getProperty("def.directory")), localFiles, localPathField);
    }

    private void initCloudFilePanel() {
        initTableView(cloudFiles, cloudPathField);
    }

    private void initTableView(TableView<FileInfo> table, TextField pathField) {
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>("Type");
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));

        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("Name");
        fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileName()));

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Size");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setCellFactory(column -> new TableCell<FileInfo, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    String text = String.format("%,d bytes", item);
                    if (item == -1L) {
                        text = "[DIR]";
                    }
                    setText(text);
                }
            }
        });

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        TableColumn<FileInfo, String> fileDateColumn = new TableColumn<>("Last modified");
        fileDateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));

        table.getColumns().addAll(fileTypeColumn, fileNameColumn, fileSizeColumn, fileDateColumn);
        table.getSortOrder().add(fileTypeColumn);

        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Path path = Paths.get(pathField.getText()).resolve(table.getSelectionModel().getSelectedItem().getFileName());
                FileType type = table.getSelectionModel().getSelectedItem().getType();
                if (Files.isDirectory(path) || type == FileType.DIRECTORY) {
                    if (table == localFiles) {
                        updateList(path, table, pathField);
                    } else {
//                        StringBuilder sb = new StringBuilder(Paths.get(pathField.getText()).normalize().toString());
//                        sb.append("\\").append(table.getSelectionModel().getSelectedItem().getFileName());
//                        listChild(sb.toString());
                        listChild(cloudPath + "/" + table.getSelectionModel().getSelectedItem().getFileName());
                    }
                }
            }
        });
    }

    private void listChild(String path) {
        Command command = new Command(CommandType.LS_CURRENT, path, new Object[]{});
        networkService.sendCommand(command, (result) -> {
            Platform.runLater(() -> {
                cloudFiles.getItems().clear();
                cloudFiles.getItems().addAll((List<FileInfo>) result.getArgs()[0]);
                cloudFiles.sort();
                cloudPathField.clear();
                cloudPathField.setText(result.getPath());
            });

        });
    }

    private void initLocalDisks() {
        disksBox.getItems().clear();
        for (Path p : FileSystems.getDefault().getRootDirectories()) {
            disksBox.getItems().add(p.toString());
        }
        disksBox.getSelectionModel().select(0);
    }

    public void updateList(Path path, TableView<FileInfo> table, TextField pathField) {
        try {
            pathField.setText(path.normalize().toAbsolutePath().toString());
            table.getItems().clear();
            table.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            table.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Unable update list of files", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void updateCloudList(Path path, TableView<FileInfo> table, TextField pathField) {
        try {
//            Path base = Paths.get(".");
//            pathField.setText(base.normalize().relativize(path).normalize().toString());
            pathField.setText(path.normalize().toAbsolutePath().toString());
            table.getItems().clear();
            table.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            table.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Unable update list of files", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void selectDisk(ActionEvent actionEvent) {
        ComboBox<String> element = (ComboBox<String>) actionEvent.getSource();
        updateList(Paths.get(element.getSelectionModel().getSelectedItem()), localFiles, localPathField);
    }

    public String getSelectedFilename() {
        if (!localFiles.isFocused()) {
            return null;
        }
        return localFiles.getSelectionModel().getSelectedItem().getFileName();
    }

    public String getCurrentPath() {
        return localPathField.getText();
    }

    public void connectToServer() {
        if (networkService == null) {
            networkService = Factory.getNetworkService();
        }
        networkService.openConnection();
        switchButtonsState();
    }

    public void disconnectFromServer() {
        if (networkService != null) {
            networkService.closeConnection();
        }
        cloudFiles.getItems().clear();
        cloudPathField.clear();
        switchButtonsState();
    }

    private void switchButtonsState() {
        btnConnect.setDisable(!btnConnect.isDisabled());
        btnDisconnect.setDisable(!btnDisconnect.isDisabled());
    }

    public void listDirs() {
        Command command = new Command(CommandType.LS_CURRENT, "CloudServer/storage", new Object[]{});
        networkService.sendCommand(command, (result) -> {
//            commandResultTextArea.clear();
//            commandResultTextArea.appendText((String) result.getArgs()[0]);
            Platform.runLater(() -> {
                cloudFiles.getItems().clear();
                cloudFiles.getItems().addAll((List<FileInfo>) result.getArgs()[0]);
                cloudFiles.sort();
                cloudPath = result.getPath();
                cloudPathField.setText(result.getPath());
            });

        });
    }


}
