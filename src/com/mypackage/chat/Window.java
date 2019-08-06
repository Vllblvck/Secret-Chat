package com.mypackage.chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
            Parent root = FXMLLoader.load(getClass().getResource("gui/loginGui.fxml"));
            client = new Client();
            mainStage = primaryStage;
            mainStage.setTitle("Secret Chat");
            mainStage.setScene(new Scene(root, 400, 200));
            mainStage.setResizable(false);
            mainStage.setOnCloseRequest(e -> client.close());
            mainStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
