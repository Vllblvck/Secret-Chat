package com.mypackage.chat;

import com.mypackage.Message;
import com.mypackage.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatClient {
    private static final int SERVER_PORT = 5000;
    protected static String username;
    protected static String usersOnline;
    protected static ChatController chatController;
    private static Socket socket;
    private static ObjectInputStream inputStream;
    private static ObjectOutputStream outputStream;
    private static Scanner scanner = new Scanner(System.in);
    private static AtomicBoolean readMsgIsRunning = new AtomicBoolean(false);
    private static AtomicBoolean sendMsgIsRunning = new AtomicBoolean(false);
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
            System.out.println("Server is not running");
            //TODO add label about that
        }
    }

    public static void sendingMsg() {
        Thread sendMsg = new Thread(() -> {
            while (sendMsgIsRunning.get()) {
            }
        });
        sendMsgIsRunning.set(true);
        sendMsg.start();
    }

    public static void readingMsg() {
        Thread readMsg = new Thread(() -> {
            while (readMsgIsRunning.get()) {
                try {
                    inMsg = (Message) inputStream.readObject();
                    if (inMsg != null) {
                        switch (inMsg.getType()) {
                            case SERVER:
                                usersOnline = inMsg.getMsg();
                                if (usersOnline != null)
                                    chatController.updateGUI();
                                break;
                            case USER:
                                break;
                        }
                    }
                } catch (SocketException e) {
                    readMsgIsRunning.set(false);
                } catch (ClassNotFoundException e) {
                    readMsgIsRunning.set(false);
                } catch (IOException e) {
                    readMsgIsRunning.set(false);
                }
            }
        });
        readMsgIsRunning.set(true);
        readMsg.start();
    }

    public static boolean login(String login, String password) {
        try {
            outMsg = new Message(MessageType.SERVER, login + "|" + password);
            outputStream.writeObject(outMsg);
            inMsg = (Message) inputStream.readObject();
            if (!inMsg.getMsg().equals("incorrect") && !inMsg.getType().equals(MessageType.USER)) {
                username = inMsg.getMsg();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void close() {
        try {
            outputStream.writeObject(new Message(MessageType.DISCONNECTED, username));
            if (outputStream != null)
                outputStream.close();
            if (inputStream != null)
                inputStream.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not close streams and socket from client side");
        }
    }
}
