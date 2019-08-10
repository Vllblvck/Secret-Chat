package com.mypackage.server;

import com.mypackage.messages.Message;
import com.mypackage.messages.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

import static com.mypackage.server.Server.userAccounts;
import static com.mypackage.server.Server.usersOnline;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private String username;
    private Message outMsg;

    ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                Message inMsg = (Message) inputStream.readObject();

                switch (inMsg.getType()) {

                    case CONNECT:
                        checkLogin(inMsg.getMsg());
                        break;

                    case DISCONNECT:
                        removeClient();
                        break;

                    case USER:
                        sendMsg(inMsg);
                        break;
                }

            } catch (IOException | ClassNotFoundException e) {
                close();
            }
        }
    }

    private void checkLogin(String loginData) throws IOException {
        if (userAccounts.contains(loginData)) {
            username = new StringTokenizer(loginData, "|").nextToken();

            if (!isLoggedIn(username)) {
                loginUser();
            } else {
                outputStream.writeObject(new Message(MessageType.SERVER, "incorrect"));
            }
        } else {
            outputStream.writeObject(new Message(MessageType.SERVER, "incorrect"));
        }
    }

    private void loginUser() throws IOException {
        outMsg = new Message(MessageType.SERVER, "correct");
        usersOnline.put(username, this);
        outputStream.writeObject(outMsg);
        sendUsersOnline();
    }

    private boolean isLoggedIn(String username) {
        return usersOnline.containsKey(username);
    }

    private void removeClient() throws IOException {
        usersOnline.remove(username);
        sendUsersOnline();
    }

    private void sendUsersOnline() throws IOException {
        outMsg = new Message(MessageType.SERVER, getUsersOnline());

        for (String user : usersOnline.keySet()) {
            usersOnline.get(user).outputStream.writeObject(outMsg);
        }
    }

    private String getUsersOnline() {
        StringBuilder usersString = new StringBuilder();

        for (String key : usersOnline.keySet()) {
            usersString.append(key).append("\n");
        }
        return usersString.toString();
    }

    private void sendMsg(Message msg) throws IOException {
        msg.setSender(username);
        usersOnline.get(msg.getRecipient()).outputStream.writeObject(msg);
    }

    private void close() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
            removeClient();
            System.out.println("User disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}