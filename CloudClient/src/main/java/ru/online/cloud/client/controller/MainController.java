package ru.online.cloud.client.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.online.cloud.client.factory.Factory;
import ru.online.cloud.client.service.NetworkService;
import ru.online.domain.FileInfo;
import ru.online.domain.FileType;
import ru.online.domain.command.Command;
import ru.online.domain.command.CommandType;

import java.io.File;
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

    public NetworkService networkService;
    private Properties properties;
    private String userCloudDirectory;

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button authenticate;
    @FXML
    private Button register;
    @FXML
    private Button btnCloudUp;
    @FXML
    private Button btnLocalUp;
    @FXML
    private Button btnRefreshCloud;
    @FXML
    private Button btnRefreshLocal;
    @FXML
    private Button btnUpload;
    @FXML
    private Button btnDownload;
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
//        loadProperties();
        connectToServer();
        initLocalFilePanel();
        initCloudFilePanel();
        disableCloudInterface(true);
    }

//    private void loadProperties() {
//        try (InputStream input = MainController.class.getClassLoader().getResourceAsStream("client.properties")) {
//            properties = new Properties();
//            properties.load(input);
//        } catch (IOException exception) {
//            exception.printStackTrace();
//        }
//    }

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
            updateList(CommandType.LS, upperPath, localFiles, localPathField);
        }
    }

    public void btnCloudPathUp() {
        Path upperPath = Paths.get(cloudPathField.getText()).getParent();
        if (upperPath != null) {
            if (getCurrentPath(cloudPathField).equals(userCloudDirectory)) {
                updateList(CommandType.LS, Paths.get(cloudPathField.getText()), cloudFiles, cloudPathField);
            } else {
                updateList(CommandType.LS, upperPath, cloudFiles, cloudPathField);
            }
        }
    }

    private void initLocalFilePanel() {
        initTableView(localFiles, localPathField);
        initLocalDisks();
//        updateList(CommandType.LS, Paths.get(properties.getProperty("def.directory")), localFiles, localPathField);
        updateList(CommandType.LS, Paths.get(System.getProperty("user.home")), localFiles, localPathField);
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
                Path path = Paths.get(getCurrentPath(pathField) + File.separator + getSelectedFilename(table));
                FileType type = table.getSelectionModel().getSelectedItem().getType();
                if (Files.isDirectory(path) || type == FileType.DIRECTORY) {
                    updateList(CommandType.LS, path, table, pathField);
                }
            }
        });
    }

    private void initLocalDisks() {
        disksBox.getItems().clear();
        for (Path p : FileSystems.getDefault().getRootDirectories()) {
            disksBox.getItems().add(p.toString());
        }
        disksBox.getSelectionModel().select(0);
    }

    public void updateList(CommandType type, Path path, TableView<FileInfo> table, TextField pathField) {
        if (table == cloudFiles) {
            Command command = new Command(type, path.toString(), new Object[]{});
            networkService.sendCommand(command, (result) -> {
                Platform.runLater(() -> {
                    table.getItems().clear();
                    table.getItems().addAll((List<FileInfo>) result.getArgs()[0]);
                    table.sort();
                    pathField.clear();
                    pathField.setText(result.getPath());
                });
            });
        } else {
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
    }

    public void selectDisk(ActionEvent actionEvent) {
        ComboBox<String> element = (ComboBox<String>) actionEvent.getSource();
        updateList(CommandType.LS, Paths.get(element.getSelectionModel().getSelectedItem()), localFiles, localPathField);
    }

    public String getSelectedFilename(TableView<FileInfo> tableView) {
        if (!tableView.isFocused()) {
            return null;
        }
        return tableView.getSelectionModel().getSelectedItem().getFileName();
    }

    public String getCurrentPath(TextField pathField) {
        return pathField.getText();
    }

    public void connectToServer() {
        if (networkService == null) {
            networkService = Factory.getNetworkService();
        }
        networkService.openConnection();
    }

    public void disconnectFromServer() {
        if (networkService != null) {
            networkService.closeConnection();
        }
        cloudFiles.getItems().clear();
        cloudPathField.clear();
    }

    public void refreshCloudDirs() {
        Command command = new Command(CommandType.LS, userCloudDirectory, new Object[]{});
        networkService.sendCommand(command, (result) -> {
            Platform.runLater(() -> {
                cloudFiles.getItems().clear();
                cloudFiles.getItems().addAll((List<FileInfo>) result.getArgs()[0]);
                cloudFiles.sort();
                cloudPathField.setText(result.getPath());
            });
        });
    }

    public void refreshLocalDirs() {
        updateList(CommandType.DUMMY, Paths.get(getCurrentPath(localPathField)), localFiles, localPathField);
    }

    private void disableLocalInterface(boolean value) {
        btnUpload.setDisable(value);
        btnDownload.setDisable(value);
        btnRefreshLocal.setDisable(value);
        localPathField.setDisable(value);
        localFiles.setDisable(value);
        disksBox.setDisable(value);
        btnLocalUp.setDisable(value);
    }

    private void disableCloudInterface(boolean value) {
        btnUpload.setDisable(value);
        btnDownload.setDisable(value);
        btnRefreshCloud.setDisable(value);
        cloudPathField.setDisable(value);
        cloudFiles.setDisable(value);
        btnCloudUp.setDisable(value);
    }

    private void disableAuthBlock() {
        username.setDisable(true);
        password.setDisable(true);
        authenticate.setDisable(true);
        register.setDisable(true);
    }

    public void btnUploadCommand() {
        String path = getCurrentPath(localPathField) + File.separator + getSelectedFilename(localFiles);
        File file = new File(path);
        if (file.isDirectory()) {
            return;
        }
        Command command = new Command(CommandType.UPLOAD, null, new Object[]{getSelectedFilename(localFiles), file.length(), userCloudDirectory});
        if (command.getArgs()[0] == null) {
            return;
        }
        networkService.sendCommand(command, (result) -> {
            command.setPath(path);
            disableCloudInterface(true);
            if (result.getCommandName() == CommandType.UPLOAD_READY) {
                networkService.sendFile(command, (res) -> {
                    if (res.getCommandName() == CommandType.UPLOAD_COMPLETE) {
                        refreshCloudDirs();
                        disableCloudInterface(false);
                    }
                });
            }
        });
    }

    public void btnDownloadCommand() {
        String path = getCurrentPath(cloudPathField) + File.separator + getSelectedFilename(cloudFiles);
        FileType type = cloudFiles.getSelectionModel().getSelectedItem().getType();
        if (type == FileType.DIRECTORY) {
            return;
        }
        File file = new File(path);
        Command command = new Command(CommandType.DOWNLOAD, "", new Object[]{});
        networkService.sendCommand(command, (result) -> {
            if (result.getCommandName() == CommandType.DOWNLOAD_READY) {
                disableLocalInterface(true);
                disableCloudInterface(true);
                networkService.sendDownloadCommand(new Command(CommandType.DOWNLOAD_READY, path, new Object[]{getSelectedFilename(cloudFiles), file.length(), getCurrentPath(localPathField)}), (res) -> {
                    if (res.getCommandName() == CommandType.DOWNLOAD_COMPLETE) {
                        disableLocalInterface(false);
                        disableCloudInterface(false);
                        refreshLocalDirs();
                    }
                });
            }
        });
    }

    public void sendRegisterCommand() {
        networkService.sendCommand(new Command(CommandType.REGISTRATION_REQUEST, "", new Object[]{username.getText(), password.getText()}), result -> {
            Platform.runLater(() -> {
                if (result.getCommandName() == CommandType.REGISTRATION_FAILED) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Registration failed", ButtonType.OK);
                    alert.showAndWait();
                }
                if (result.getCommandName() == CommandType.REGISTRATION_SUCCEEDED) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Registration succeed", ButtonType.OK);
                    alert.showAndWait();
                }
                username.clear();
                password.clear();
            });
        });
    }

    public void sendAuthenticateCommand() {
        networkService.sendCommand(new Command(CommandType.AUTH_REQUEST, "", new Object[]{username.getText(), password.getText()}), result -> {
            Platform.runLater(() -> {
                if (result.getCommandName() == CommandType.AUTH_FAILED) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Auth failed", ButtonType.OK);
                    alert.showAndWait();
                }
                if (result.getCommandName() == CommandType.AUTH_SUCCEEDED) {
                    userCloudDirectory = (String) result.getArgs()[0];
                    refreshCloudDirs();
                    disableAuthBlock();
                    disableCloudInterface(false);
                }
                username.clear();
                password.clear();
            });
        });
    }

    //TODO: refactoring, progress bar, mkDir, remDir, logOut+reLogon, interface alignment, add logger

}
