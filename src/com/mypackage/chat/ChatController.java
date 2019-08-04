package com.mypackage.chat;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.StringTokenizer;


public class ChatController implements Initializable {
    @FXML
    private Label username;
    @FXML
    private ListView<String> usersOnline;
    @FXML
    private ListView<String> messages;
    @FXML
    private Label usersCounter;
    @FXML
    private TextArea message;

    public void updateUsersList() {
        Platform.runLater(() -> {
            StringTokenizer tokenizer = new StringTokenizer(ChatClient.usersOnline, "\n");
            usersCounter.setText(Integer.toString(tokenizer.countTokens() - 1));
            usersOnline.getItems().clear();
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (!token.equals(ChatClient.username))
                    usersOnline.getItems().add(token);
            }
        });
    }

    public void sendMessage() {
        ChatClient.recipient = usersOnline.getSelectionModel().getSelectedItem();
        if (ChatClient.recipient != null) {
            String toSend = message.getText().replaceAll("\n", System.getProperty("line.separator"));
            ChatClient.sendMsg(toSend);
            displayMessage("You:" + toSend);
        }
    }

    public void displayMessage(String msg) {
        Platform.runLater(() -> messages.getItems().add(msg));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        username.setText(ChatClient.username);
    }

}