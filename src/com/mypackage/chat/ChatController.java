package com.mypackage.chat;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

public class ChatController implements Initializable {
    @FXML
    private Label username;
    @FXML
    private ListView<String> usersOnline;
    @FXML
    private Label usersCounter;

    public void updateGUI() {
        Platform.runLater(() -> {
            ChatClient.readUsersOnline();
            StringTokenizer tokenizer = new StringTokenizer(ChatClient.usersOnline, "\n");
            usersCounter.setText(Integer.toString(tokenizer.countTokens()));
            while (tokenizer.hasMoreTokens())
                usersOnline.getItems().add(tokenizer.nextToken());
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //StringTokenizer tokenizer = new StringTokenizer(ChatClient.usersOnline, "\n");
        username.setText(ChatClient.username);
        //usersCounter.setText(Integer.toString(tokenizer.countTokens()));
        /*while (tokenizer.hasMoreTokens())
            this.usersOnline.getItems().add(tokenizer.nextToken());*/
    }
}