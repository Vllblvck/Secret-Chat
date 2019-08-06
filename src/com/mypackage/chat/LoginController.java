package com.mypackage.chat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.mypackage.chat.Window.client;
import static com.mypackage.chat.Window.mainStage;

public class LoginController implements Initializable {
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    @FXML
    private Button loginButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginButton.setOnAction(event -> {
            try {
                if (client.login(login.getText(), password.getText())) { //TODO highliting text filed when login data is wrong or add label when user is already logged in
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/chatGui.fxml"));
                    Parent root = loader.load();

                    mainStage.setScene(new Scene(root, 900, 600));
                    client.setChatController(loader.getController());
                    client.readingMsg();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
