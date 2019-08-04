package com.mypackage.server;

import com.mypackage.messages.Message;
import com.mypackage.messages.MessageType;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

import static com.mypackage.server.ChatServer.clientsOnline;
import static com.mypackage.server.ChatServer.userAccounts;

public class ClientHandler implements Runnable {
    private Message inMsg;
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
                inMsg = (Message) inputStream.readObject();

                switch (inMsg.getType()) {
                    case CONNECT:
                        login(inMsg.getMsg());
                        break;
                    case DISCONNECT:
                        removeClient(inMsg.getMsg());
                        sendClientsOnline();
                        break;
                    case USER:
                        sendMessage(inMsg);
                        break;
                }

            } catch (EOFException e) {
                close();
                removeClient(this.username);
            } catch (ClassNotFoundException e) {
                close();
                removeClient(this.username);
            } catch (IOException e) {
                close();
                removeClient(this.username);
            }
        }
    }

    private void sendMessage(Message msg) throws IOException {
        for (ClientHandler client : clientsOnline) {
            if(client.username.equals(msg.getRecipient())) {
                client.outputStream.writeObject(msg);
            }
        }
    }

    private void login(String loginData) {
        try {
            if (checkLogin(loginData)) {
                outputStream.writeObject(new Message(MessageType.CONNECT, this.username));
                sendClientsOnline();
            } else
                outputStream.writeObject(new Message(MessageType.CONNECT, "incorrect"));
        } catch (EOFException e) {
            close();
            removeClient(this.username);
        } catch (IOException e) {
            close();
            removeClient(this.username);
        }
    }

    private boolean checkLogin(String loginData) {
        for (String useraccount : userAccounts) {
            if (useraccount.equals(loginData)) {
                StringTokenizer tokenizer = new StringTokenizer(loginData, "|");
                username = tokenizer.nextToken();
                if (!isLoggedIn(username)) {
                    clientsOnline.add(this);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isLoggedIn(String username) {
        for (ClientHandler client : clientsOnline) {
            if (client.username.equals(username))
                return true;
        }
        return false;
    }

    private void sendClientsOnline() throws IOException {
        for (ClientHandler client : clientsOnline) {
            client.outputStream.writeObject(new Message(MessageType.CONNECT, getClientsOnline()));
        }
    }

    private String getClientsOnline() {
        StringBuffer clientsString = new StringBuffer();

        for (ClientHandler client : clientsOnline) {
            clientsString.append(client.username + "\n");
        }
        return clientsString.toString();
    }

    private void removeClient(String name) {
        for (ClientHandler client : clientsOnline) {
            if (client.username.equals(name)) {
                clientsOnline.remove(client);
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