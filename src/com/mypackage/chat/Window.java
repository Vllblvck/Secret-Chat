package com.mypackage.chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Window extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            ChatClient.connectToServer();
            Parent root = FXMLLoader.load(getClass().getResource("loginGui.fxml"));
            primaryStage.setTitle("Tajny czat");
            primaryStage.setScene(new Scene(root, 400, 200));
            primaryStage.setResizable(false);
            primaryStage.setOnCloseRequest(e -> ChatClient.close());
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
