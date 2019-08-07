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
    private Label serverStatus;
    @FXML
    private AnchorPane window;

    private double xOffset;
    private double yOffset;

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

        loginButton.setOnAction(event -> {
            try {
                if (client.login(usernameField.getText(), passwordField.getText())) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/ChatGui.fxml"));
                    Parent root = loader.load();
                    client.setChatController(loader.getController());
                    client.readingMsg();
                    mainStage.setScene(new Scene(root, 900, 600));
                } else {
                    //TODO highlight text filed when login data is wrong
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        exitButton.setOnAction(event -> client.close());
        minimizeButton.setOnAction(event -> mainStage.setIconified(true));
        hyperlink.setOnAction(event -> {
            new Window().goToURL("https://www.facebook.com");
        });
    }

    public void setServerStatus(String serverStatus) {
        this.serverStatus.setText(serverStatus);
        if (serverStatus.equals("ON"))
            this.serverStatus.setTextFill(Color.GREEN);
        else
            this.serverStatus.setTextFill(Color.RED);
    }
}
