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
import java.util.HashMap;
import java.util.StringTokenizer;

public class Client {
    private final int SERVER_PORT = 5000;
    protected HashMap<String, ObservableList<String>> chatWindows = new HashMap<>();
    private String usersOnline;
    private ChatController chatController;
    private String recipient;
    private String username;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private boolean readMsgRunning = false;

    public Client() {
        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket("127.0.0.1", SERVER_PORT);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            //TODO add label about server not running
        }
    }

    public void sendMsg(String msg) {
        try {
            Message outMsg = new Message(MessageType.USER, recipient, username, msg);
            outputStream.writeObject(outMsg);
            chatWindows.get(outMsg.getRecipient()).add("You: " + outMsg.getMsg());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readingMsg() {
        Thread readMsg = new Thread(() -> {
            while (readMsgRunning) {
                try {
                    Message inMsg = (Message) inputStream.readObject();

                    switch (inMsg.getType()) {

                        case CONNECT:
                            usersOnline = inMsg.getMsg();
                            chatController.updateUsersList();
                            addToChatWindows();
                            break;

                        case USER:
                            Platform.runLater(() -> chatWindows.get(inMsg.getSender()).add(inMsg.getSender() + ": " + inMsg.getMsg()));
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

    private void addToChatWindows() {
        StringTokenizer tokenizer = new StringTokenizer(usersOnline, "\n");

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();

            if (!token.equals(username)) {
                if (!chatWindows.containsKey(token))
                    chatWindows.put(token, FXCollections.observableArrayList());
            }
        }
    }

    protected boolean login(String login, String password) {
        try {
            Message outMsg = new Message(MessageType.CONNECT, login + "|" + password);
            outputStream.writeObject(outMsg);
            Message inMsg = (Message) inputStream.readObject();

            if (!inMsg.getMsg().equals("incorrect") && inMsg.getType().equals(MessageType.CONNECT)) {
                username = inMsg.getMsg();
                return true;
            }

        } catch (IOException | ClassNotFoundException e) {
            close();
        }
        return false;
    }

    protected void close() {
        try {
            if (outputStream != null) {
                outputStream.writeObject(new Message(MessageType.DISCONNECT, username));
                outputStream.close();
            }
            if (inputStream != null)
                inputStream.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsersOnline() {
        return usersOnline;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getUsername() {
        return username;
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }
}
