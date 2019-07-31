package com.mypackage.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatClient {
    private final int SERVER_PORT = 5000;
    private Scanner scanner = new Scanner(System.in);
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private AtomicBoolean sendMsgIsRunning = new AtomicBoolean(false);
    private AtomicBoolean readMsgIsRunning = new AtomicBoolean(false);
    private String username;
    private String usersOnline;

    public ChatClient() {
        try {
            socket = new Socket("127.0.0.1", SERVER_PORT);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            //sendingMsg();
            //readingMsg();
        } catch (IOException e) {
            //TODO add label about that
            System.out.println("Server is not running");
        }
    }

    /*@Override
    public void run() {
        //getUsersOnline()
        //sengingMsg.start
        //readingMsg.start
    }*/

    public void sendingMsg() {
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

    public void readingMsg() {
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

    public boolean login(String login, String password) throws IOException {
        outputStream.writeUTF(login + "|" + password);
        if (inputStream.readUTF().equals("logged in")) {
            username = inputStream.readUTF();
            usersOnline = inputStream.readUTF();
            return true;
        }
        return false;
    }

    public void close() throws IOException {
        outputStream.close();
        inputStream.close();
        socket.close();
    }

    public String getUsername() {
        return username;
    }

    public String getUsersOnline() {
        return usersOnline;
    }
}
