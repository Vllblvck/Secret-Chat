package com.mypackage.chat;

import com.mypackage.messages.Message;
import com.mypackage.messages.MessageType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

class Client {
    HashMap<String, ObservableList<String>> chatWindows = new HashMap<>();
    ArrayList<String> usersOnline = new ArrayList<>();
    private ChatController chatController;
    private LoginController loginController;
    private String username;
    private String recipient;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private boolean readMsgRunning = false;
    private Message inMsg;
    private Message outMsg;

    Client(LoginController loginController) {
        this.loginController = loginController;
        connect();
    }

    private void connect() {
        try {
            socket = new Socket("127.0.0.1", 5000);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            loginController.setServerStatus("ON");
        } catch (IOException e) {
            loginController.setServerStatus("OFF");
        }
    }

    boolean loginRequest(String login, String password) {
        try {
            outMsg = new Message(MessageType.CONNECT, login + "|" + password);
            outputStream.writeObject(outMsg);
            inMsg = (Message) inputStream.readObject();

            if (inMsg.getMsg().equals("correct") && inMsg.getType().equals(MessageType.SERVER)) {
                return true;
            }

        } catch (IOException | ClassNotFoundException e) {
            close();
        }
        return false;
    }

    void readingMsg() {
        Thread readMsg = new Thread(() -> {
            while (readMsgRunning) {
                try {
                    inMsg = (Message) inputStream.readObject();
                    switch (inMsg.getType()) {

                        case SERVER:
                            updateUsersOnline(inMsg.getMsg());
                            updateChatWindows();
                            chatController.updateUsersList();
                            break;

                        case USER:
                            handleMsg();
                            break;
                    }

                } catch (ClassNotFoundException | IOException e) {
                    readMsgRunning = false;
                }
            }
        });

        readMsgRunning = true;
        readMsg.start();
    }

    void sendMsg(String msg) {
        try {
            outMsg = new Message(MessageType.USER, recipient, msg);
            outputStream.writeObject(outMsg);
            chatWindows.get(outMsg.getRecipient()).add("You: " + outMsg.getMsg());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateUsersOnline(String usersOnline) {
        StringTokenizer tokenizer = new StringTokenizer(usersOnline, "\n");
        this.usersOnline.clear();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (!token.equals(username)) {
                this.usersOnline.add(token);
            }
        }
    }

    private void updateChatWindows() {
        for (String user : usersOnline) {
            if (!chatWindows.containsKey(user)) {
                chatWindows.put(user, FXCollections.observableArrayList());
            }
        }
    }

    private void handleMsg() {
        Platform.runLater(() -> chatWindows.get(inMsg.getSender()).add(inMsg.getSender() + ": " + inMsg.getMsg()));
    }

    void close() {
        try {

            if (outputStream != null) {
                outMsg = new Message(MessageType.DISCONNECT, username);
                outputStream.writeObject(outMsg);
                outputStream.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }

            if (socket != null) {
                socket.close();
            }

            Platform.exit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String getRecipient() {
        return recipient;
    }

    void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    void setUsername(String username) {
        this.username = username;
    }

    void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }
}
