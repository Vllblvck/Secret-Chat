package com.mypackage.chat;

import com.mypackage.messages.Message;
import com.mypackage.messages.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatClient {
    private static final int SERVER_PORT = 5000;
    protected static String username;
    protected static String usersOnline;
    protected static ChatController chatController;
    protected static String recipient;
    private static Socket socket;
    private static ObjectInputStream inputStream;
    private static ObjectOutputStream outputStream;
    private static AtomicBoolean readMsgIsRunning = new AtomicBoolean(false);
    private static Message outMsg;
    private static Message inMsg;

    private ChatClient() {
    }

    public static void connectToServer() {
        try {
            socket = new Socket("127.0.0.1", SERVER_PORT);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            //TODO add label about server not running
        }
    }

    //TODO sending messages to specified users
    public static void sendMsg(String msg) {
        try {
            outputStream.writeObject(new Message(MessageType.USER, recipient, msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readingMsg() {
        Thread readMsg = new Thread(() -> {

            while (readMsgIsRunning.get()) {
                try {
                    inMsg = (Message) inputStream.readObject();

                    switch (inMsg.getType()) {

                        case CONNECT:
                            usersOnline = inMsg.getMsg();
                            chatController.updateUsersList();
                            break;

                        case USER:
                            chatController.displayMessage(inMsg.getRecipient() + ":" + inMsg.getMsg());
                            break;
                    }

                } catch (ClassNotFoundException | IOException e) {
                    readMsgIsRunning.set(false);
                }
            }
        });

        readMsgIsRunning.set(true);
        readMsg.start();
    }

    public static boolean login(String login, String password) {
        try {
            outMsg = new Message(MessageType.CONNECT, login + "|" + password);
            outputStream.writeObject(outMsg);
            inMsg = (Message) inputStream.readObject();

            if (!inMsg.getMsg().equals("incorrect") && inMsg.getType().equals(MessageType.CONNECT)) {
                username = inMsg.getMsg();
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
            close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            close();
        }
        return false;
    }

    public static void close() {
        try {
            outputStream.writeObject(new Message(MessageType.DISCONNECT, username));
            if (outputStream != null)
                outputStream.close();
            if (inputStream != null)
                inputStream.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
