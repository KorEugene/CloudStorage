package ru.online.cloud.client.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.online.cloud.client.factory.Factory;
import ru.online.cloud.client.model.FileInfo;
import ru.online.cloud.client.service.ClientService;
import ru.online.cloud.client.service.NetworkService;
import ru.online.domain.Command;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    public TextField commandTextField;
    public TextArea commandResultTextArea;

    public ClientService clientService;
    public NetworkService networkService;

    @FXML
    private Button btnConnect;
    @FXML
    private Button btnDisconnect;
    @FXML
    private TextField pathField;
    @FXML
    private ComboBox<String> disksBox;
    @FXML
    private TableView<FileInfo> localFiles;
    @FXML
    private TableView<FileInfo> cloudFiles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnDisconnect.setDisable(true);
        createCommandResultHandler();

        initializePanel();
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
        String[] textCommand = commandTextField.getText().trim().split("\\s");
        if (textCommand.length > 1) {
            String[] commandArgs = Arrays.copyOfRange(textCommand, 1, textCommand.length);
            networkService.sendCommand(new Command(textCommand[0], commandArgs));
            commandTextField.clear();
        }
    }

    public void shutdown() {
//        networkService.closeConnection();
    }


    public void btnCloseApp(ActionEvent actionEvent) {
        networkService.closeConnection();
        Platform.exit();
    }

    public void btnPathUp(ActionEvent actionEvent) {
        Path upperPath = Paths.get(pathField.getText()).getParent();
        if (upperPath != null) {
            updateList(upperPath);
        }
    }

    public void initializePanel() {
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

        localFiles.getColumns().addAll(fileTypeColumn, fileNameColumn, fileSizeColumn, fileDateColumn);
        localFiles.getSortOrder().add(fileTypeColumn);

        disksBox.getItems().clear();
        for (Path p : FileSystems.getDefault().getRootDirectories()) {
            disksBox.getItems().add(p.toString());
        }
        disksBox.getSelectionModel().select(0);

        localFiles.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Path path = Paths.get(pathField.getText()).resolve(localFiles.getSelectionModel().getSelectedItem().getFileName());
                if (Files.isDirectory(path)) {
                    updateList(path);
                }
            }
        });

        updateList(Paths.get("C:\\Users"));
    }

    public void updateList(Path path) {
        try {
            pathField.setText(path.normalize().toAbsolutePath().toString());
            localFiles.getItems().clear();
            localFiles.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            localFiles.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Unable update list of files", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void selectDisk(ActionEvent actionEvent) {
        ComboBox<String> element = (ComboBox<String>) actionEvent.getSource();
        updateList(Paths.get(element.getSelectionModel().getSelectedItem()));
    }

    public String getSelectedFilename() {
        if (!localFiles.isFocused()) {
            return null;
        }
        return localFiles.getSelectionModel().getSelectedItem().getFileName();
    }

    public String getCurrentPath() {
        return pathField.getText();
    }

    public void connectToServer(ActionEvent actionEvent) {
        if (clientService == null) {
            clientService = Factory.getClientService();
        } else {
            clientService.startClient();
        }
        networkService = Factory.getNetworkService();
        switchButtonsState();
    }

    public void disconnectFromServer(ActionEvent actionEvent) {
        clientService.stopClient();
        switchButtonsState();
    }

    private void switchButtonsState() {
        btnConnect.setDisable(!btnConnect.isDisabled());
        btnDisconnect.setDisable(!btnDisconnect.isDisabled());
    }
}
