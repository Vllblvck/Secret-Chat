package com.mypackage.chat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.mypackage.chat.Window.client;
import static com.mypackage.chat.Window.mainStage;

public class LoginController implements Initializable {
    @FXML
    private Label serverStatus;
    @FXML
    private Label wrongData;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button minimizeButton;
    @FXML
    private Hyperlink hyperlink;
    @FXML
    private AnchorPane anchorPane;

    private double xOffset;
    private double yOffset;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setWindowDrag();
        setLoginButtonAction();
        setWindowButtons();
        setHyperlink();
    }

    private void setWindowDrag() {
        anchorPane.setOnMousePressed(mouseEvent -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });

        anchorPane.setOnMouseDragged(mouseEvent -> {
            mainStage.setX(mouseEvent.getScreenX() - xOffset);
            mainStage.setY(mouseEvent.getScreenY() - yOffset);
        });
    }

    private void setLoginButtonAction() {
        loginButton.setOnAction(event -> {
            try {
                if (client.loginRequest(usernameField.getText(), passwordField.getText())) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/ChatGui.fxml"));
                    Parent root = loader.load();
                    client.setUsername(usernameField.getText());
                    client.setChatController(loader.getController());
                    client.readingMsg();
                    mainStage.setScene(new Scene(root, 900, 600));
                } else {
                    wrongData.setText("Wrong username or password!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void setWindowButtons() {
        exitButton.setOnAction(event -> client.close());
        minimizeButton.setOnAction(event -> mainStage.setIconified(true));
    }

    private void setHyperlink() {
        hyperlink.setOnAction(event -> {
            new Window().goToURL("https://www.facebook.com");
        });
    }

    void setServerStatus(String serverStatus) {
        this.serverStatus.setText(serverStatus);
        if (serverStatus.equals("ON"))
            this.serverStatus.setTextFill(Color.GREEN);
        else
            this.serverStatus.setTextFill(Color.RED);
    }
}
