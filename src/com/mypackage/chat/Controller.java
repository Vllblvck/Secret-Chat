package com.mypackage.chat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class Controller {
    private ChatClient chatClient;
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    private Scene chatScene;
    @FXML
    private ListView<String> usersOnline = new ListView<>();
    @FXML
    private Label username = new Label();

    public Controller() {
        chatClient = new ChatClient();
    }

    public void onClose() {
        try {
            chatClient.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not close streams");
        }
    }

    public void loginButtonPush() {
        try {
            if (chatClient.login(login.getText(), password.getText())) {
                switchToChatScene();
            } //TODO highliting text filed when login data is wrong
        } catch (IOException e) {
            //TODO add label that indicates this exception
            System.out.println("Something went wrong with logging in");
        }
    }

    public void switchToChatScene() throws IOException {
        setupChatScene();
        Parent root = FXMLLoader.load(getClass().getResource("chatGui.fxml"));
        chatScene = new Scene(root, 900, 600);
        Window.window.setScene(chatScene);
        Window.window.setOnCloseRequest(e -> onClose());
    }

    public void setupChatScene() {
        username.setText(chatClient.getUsername());
        usersOnline.getItems().add(chatClient.getUsersOnline());
    }
}
