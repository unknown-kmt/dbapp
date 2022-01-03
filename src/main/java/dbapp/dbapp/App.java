package dbapp.dbapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class App extends Application {

    private static final String url = "jdbc:postgresql://127.0.0.1/disksdb";
    private static final String user = "postgres";
    private static final String password = "user";

    @Override
    public void start(Stage stage) throws IOException {
        DBConnection dbCon = new DBConnection(url, user, password);
        DBTalker talker = new DBTalker(dbCon);

        ViewController.setTalker(talker);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() { // close operation request
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("app-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Дискотека");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}