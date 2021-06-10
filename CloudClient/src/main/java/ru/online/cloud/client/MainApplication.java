package ru.online.cloud.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.online.cloud.client.controller.MainController;

import java.io.InputStream;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/mainWindow.fxml"));
        Parent parent = loader.load();
        primaryStage.setTitle("Cloud Client");
        primaryStage.setScene(new Scene(parent, 800, 600));
        primaryStage.setResizable(true);
        InputStream clientIconStream = MainApplication.class.getResourceAsStream("/img/cloud_client.png");
        Image chatIcon = new Image(clientIconStream);
        primaryStage.getIcons().add(chatIcon);

        MainController controller = loader.getController();
        primaryStage.setOnCloseRequest((event) -> controller.shutdown());
        primaryStage.show();
    }
}
