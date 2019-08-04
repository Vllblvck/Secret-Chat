package com.mypackage.chat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    protected TextField login;
    @FXML
    protected PasswordField password;
    @FXML
    protected Button loginButton;

    public void loginButtonPush(ActionEvent event) {
        try {
            if(ChatClient.login(login.getText(),password.getText())) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("chatGui.fxml"));
                Parent root = loader.load();
                Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                primaryStage.setScene(new Scene(root, 900, 600));
                ChatClient.chatController = loader.getController();
                ChatClient.readingMsg();
                //TODO highliting text filed when login data is wrong
            }
        } catch (IOException e) {
            //TODO add label that indicates this exception
            e.printStackTrace();
            System.out.println("Something went wrong with logging in");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
