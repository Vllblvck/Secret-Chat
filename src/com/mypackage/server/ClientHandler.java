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

    public ClientHandler(Socket socket) throws IOException {
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
                        removeClient(inMsg.getMsg());
                        sendUsersOnline();
                        break;

                    case USER:
                        sendMsg(inMsg);
                        break;
                }

            } catch (IOException | ClassNotFoundException e) {
                close();
                removeClient(this.username);
            }
        }
    }

    private void checkLogin(String loginData) throws IOException {
        boolean correctData = false;

        for (String useraccount : userAccounts) {
            if (useraccount.equals(loginData)) {
                StringTokenizer tokenizer = new StringTokenizer(loginData, "|");
                username = tokenizer.nextToken();
                correctData = true;

                if (!isLoggedIn(username)) {
                    usersOnline.add(this);
                    loginUser();
                    break;
                } else {
                    outputStream.writeObject(new Message(MessageType.CONNECT, "incorrect"));
                    break;
                }
            }
        }

        if (!correctData) {
            outputStream.writeObject(new Message(MessageType.CONNECT, "incorrect"));
        }
    }

    private void loginUser() throws IOException {
        outputStream.writeObject(new Message(MessageType.CONNECT, this.username));
        sendUsersOnline();
    }

    private boolean isLoggedIn(String username) {
        for (ClientHandler client : usersOnline) {
            if (client.username.equals(username))
                return true;
        }
        return false;
    }

    private void removeClient(String name) {
        for (ClientHandler client : usersOnline) {
            if (client.username.equals(name)) {
                usersOnline.remove(client);
                break;
            }
        }
    }

    private void sendUsersOnline() throws IOException {
        for (ClientHandler client : usersOnline) {
            client.outputStream.writeObject(new Message(MessageType.CONNECT, getUsersOnline()));
        }
    }

    private String getUsersOnline() {
        StringBuffer clientsString = new StringBuffer();

        for (ClientHandler client : usersOnline) {
            clientsString.append(client.username + "\n");
        }
        return clientsString.toString();
    }

    private void sendMsg(Message msg) throws IOException {
        for (ClientHandler client : usersOnline) {
            if (client.username.equals(msg.getRecipient())) {
                client.outputStream.writeObject(msg);
                break;
            }
        }
    }

    private void close() {
        try {
            if (inputStream != null)
                inputStream.close();
            if (outputStream != null)
                outputStream.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}