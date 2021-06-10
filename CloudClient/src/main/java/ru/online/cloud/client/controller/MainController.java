package ru.online.cloud.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.online.cloud.client.factory.Factory;
import ru.online.cloud.client.service.NetworkService;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public TextField commandTextField;
    public TextArea commandResultTextArea;

    public NetworkService networkService;

    @FXML
    public TableView localFiles;
    @FXML
    public TableView cloudFiles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        networkService = Factory.getNetworkService();

        createCommandResultHandler();
    }

    private void createCommandResultHandler() {
        new Thread(() -> {
            while (true) {
                String resultCommand = networkService.readCommandResult();
                Platform.runLater(() -> commandResultTextArea.appendText(resultCommand + System.lineSeparator()));
            }
        }).start();
    }

    public void sendCommand(ActionEvent actionEvent) {
        networkService.sendCommand(commandTextField.getText().trim());
        commandTextField.clear();
    }

    public void shutdown() {
        networkService.closeConnection();
    }


}
