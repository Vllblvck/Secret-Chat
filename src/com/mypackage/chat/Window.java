package com.mypackage.chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Window extends Application {
    protected static Stage window;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        try {
            Parent root = FXMLLoader.load(getClass().getResource("loginGui.fxml"));
            window.setTitle("Tajny czat");
            window.setScene(new Scene(root, 400, 200));
            window.setResizable(false);
            window.setOnCloseRequest(e -> new Controller().onClose());
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
