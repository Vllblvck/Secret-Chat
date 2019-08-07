package com.mypackage.chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Window extends Application {
    protected static Client client;
    protected static Stage mainStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/LoginGui.fxml"));
            Parent root = loader.load();
            client = new Client(loader.getController());
            mainStage = primaryStage;
            mainStage.initStyle(StageStyle.UNDECORATED);
            mainStage.setScene(new Scene(root, 600, 425));
            mainStage.setResizable(false);
            mainStage.setOnCloseRequest(e -> client.close());
            mainStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToURL(String url) {
        getHostServices().showDocument(url);
    }
}
