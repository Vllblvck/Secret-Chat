package com.mypackage.chat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    protected TextField login;
    @FXML
    protected PasswordField password;
    @FXML
    protected Button loginButton;

    public void loginButtonPush(ActionEvent event) {
        try {
            if (ChatClient.login(login.getText(), password.getText())) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/chatGui.fxml"));
                Parent root = loader.load();
                Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                primaryStage.setScene(new Scene(root, 900, 600));
                ChatClient.chatController = loader.getController();
                ChatClient.readingMsg();
                //TODO highliting text filed when login data is wrong or add label when users is already logged in
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
