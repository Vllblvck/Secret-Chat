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

import static com.mypackage.chat.Window.client;

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

    @FXML
    private void sendButtonClick() {
        client.setRecipient(usersOnline.getSelectionModel().getSelectedItem());

        if (client.getRecipient() != null) {
            String toSend = message.getText().replaceAll("\n", System.getProperty("line.separator"));
            client.sendMsg(toSend);
        }
    }

    public void updateUsersList() {
        Platform.runLater(() -> {
            StringTokenizer tokenizer = new StringTokenizer(client.getUsersOnline(), "\n");
            usersCounter.setText(Integer.toString(tokenizer.countTokens() - 1));
            usersOnline.getItems().clear();

            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (!token.equals(client.getUsername()))
                    usersOnline.getItems().add(token);
            }
        });
    }

    private void displayMessage(String newValue) {
        Platform.runLater(() -> messages.setItems(client.chatWindows.get(newValue)));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        username.setText(client.getUsername());
        usersOnline.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> displayMessage(newValue));
    }

}