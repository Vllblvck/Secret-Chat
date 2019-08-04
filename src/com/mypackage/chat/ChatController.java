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
            StringTokenizer tokenizer = new StringTokenizer(ChatClient.usersOnline, "\n");
            usersCounter.setText(Integer.toString(tokenizer.countTokens() - 1));
            usersOnline.getItems().clear();
            while (tokenizer.hasMoreTokens())
                //TODO dont add this.user to list
                usersOnline.getItems().add(tokenizer.nextToken());
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        username.setText(ChatClient.username);
    }

}