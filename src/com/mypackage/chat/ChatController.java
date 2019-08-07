package com.mypackage.chat;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import static com.mypackage.chat.Window.client;
import static com.mypackage.chat.Window.mainStage;

public class ChatController implements Initializable {
    @FXML
    private Label username;
    @FXML
    private ListView<String> usersOnline;
    @FXML
    private ListView<String> messages;
    @FXML
    private TextArea message;
    @FXML
    private Button exitButton;
    @FXML
    private Button minimizeButton;
    @FXML
    private BorderPane window;

    private double xOffset;
    private double yOffset;

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
        window.setOnMousePressed(mouseEvent -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });

        window.setOnMouseDragged(mouseEvent -> {
            mainStage.setX(mouseEvent.getScreenX() - xOffset);
            mainStage.setY(mouseEvent.getScreenY() - yOffset);
        });

        username.setText("LOGGED IN AS: " + client.getUsername());
        usersOnline.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> displayMessage(newValue));

        exitButton.setOnAction(event -> client.close());
        minimizeButton.setOnAction(event -> mainStage.setIconified(true));
    }

}