package dbapp.dbapp;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Alerter {

    public static void alertError(String massage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setGraphic(null);
        alert.setContentText(massage);
        alert.getDialogPane().setMinSize(200, 100);
        alert.showAndWait();
    }

    public static void alertWarning(String massage) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setGraphic(null);
        alert.setContentText(massage);
        alert.getDialogPane().setMinSize(200, 100);
        alert.showAndWait();
    }

    public static void alertMassage(String massage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setGraphic(null);
        alert.setContentText(massage);
        alert.getDialogPane().setMinSize(200, 100);
        alert.showAndWait();
    }

}
