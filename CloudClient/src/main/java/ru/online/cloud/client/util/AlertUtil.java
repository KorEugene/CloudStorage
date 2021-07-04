package ru.online.cloud.client.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertUtil {

    private AlertUtil() {
    }

    public static Alert getAlert(Alert.AlertType type, String text) {
        return new Alert(type, text, ButtonType.OK);
    }
}
