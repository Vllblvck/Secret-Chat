package com.mypackage.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatClient {
    private static final int SERVER_PORT = 5000;
    protected static String username;
    protected static String usersOnline;
    private static Socket socket;
    private static DataInputStream inputStream;
    private static DataOutputStream outputStream;
    private static Scanner scanner = new Scanner(System.in);
    private static AtomicBoolean sendMsgIsRunning = new AtomicBoolean(false);
    private static AtomicBoolean readMsgIsRunning = new AtomicBoolean(false);

    private ChatClient() {
    }

    public static void connectToServer() {
        try {
            socket = new Socket("127.0.0.1", SERVER_PORT);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Server is not running");
            //TODO add label about that
        }
    }

    public static void readUsersOnline() {
        try {
            usersOnline = inputStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void sendingMsg() {
        Thread sendMsg = new Thread(() -> {
            while (sendMsgIsRunning.get()) {
                String msg = scanner.nextLine();
                try {
                    outputStream.writeUTF(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                    sendMsgIsRunning.set(false);
                }
            }
        });
        sendMsgIsRunning.set(true);
        sendMsg.start();
    }

    public static void readingMsg() {
        Thread readMsg = new Thread(() -> {
            while (readMsgIsRunning.get()) {
                try {
                    String msg = inputStream.readUTF();
                    System.out.println(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                    readMsgIsRunning.set(false);
                }
            }
        });
        readMsgIsRunning.set(true);
        readMsg.start();
    }

    public static boolean login(String login, String password) throws IOException {
        outputStream.writeUTF(login + "|" + password);
        if (inputStream.readUTF().equals("logged in")) {
            username = inputStream.readUTF();
            usersOnline = inputStream.readUTF();
            return true;
        }
        return false;
    }

    public static void close() {
        try {
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
