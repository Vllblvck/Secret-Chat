package com.mypackage.chat;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

import static com.mypackage.chat.Window.client;
import static com.mypackage.chat.Window.mainStage;

public class ChatController implements Initializable {
    @FXML
    private ListView<String> usersOnline;
    @FXML
    private ListView<String> messages;
    @FXML
    private TextField message;
    @FXML
    private Button exitButton;
    @FXML
    private Button minimizeButton;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Button sendButton;

    private double xOffset;
    private double yOffset;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setWindowDrag();
        setWindowButtons();
        setSendButton();
        usersOnline.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> displayMessage(newValue));
    }

    private void setWindowDrag() {
        borderPane.setOnMousePressed(mouseEvent -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });

        borderPane.setOnMouseDragged(mouseEvent -> {
            mainStage.setX(mouseEvent.getScreenX() - xOffset);
            mainStage.setY(mouseEvent.getScreenY() - yOffset);
        });
    }

    private void setWindowButtons() {
        exitButton.setOnAction(event -> client.close());
        minimizeButton.setOnAction(event -> mainStage.setIconified(true));
    }

    private void setSendButton() {
        sendButton.setOnAction(actionEvent -> {
            client.setRecipient(usersOnline.getSelectionModel().getSelectedItem());

            if (client.getRecipient() != null) {
                client.sendMsg(message.getText());
            }
            message.clear();
        });
    }

    void updateUsersList() {
        Platform.runLater(() -> usersOnline.setItems(FXCollections.observableList(client.usersOnline)));
    }

    private void displayMessage(String newValue) {
        Platform.runLater(() -> messages.setItems(client.chatWindows.get(newValue)));
    }

}