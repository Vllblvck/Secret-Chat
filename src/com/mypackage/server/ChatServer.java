package com.mypackage.server;

import com.mypackage.Message;
import com.mypackage.MessageType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

public class ChatServer {
    private final int SERVER_PORT = 5000;
    private final String FILE_PATH = "useraccounts.txt";
    private Vector<ClientHandler> clientsOnline = new Vector<>();
    private ServerSocket serverSocket;
    private Socket socket;
    private ArrayList<String> userAccounts = new ArrayList<>();

    public ChatServer() throws IOException {
        serverSocket = new ServerSocket(SERVER_PORT);
        loadAccounts();
        connectClient();
    }

    public static void main(String[] args) {
        try {
            new ChatServer();
        } catch (IOException e) {
            System.out.println("Failed to run server");
            e.printStackTrace();
        }
    }

    public void connectClient() throws IOException {
        while (true) {
            System.out.println("Waiting for new connection");
            socket = serverSocket.accept();
            new Thread(new ClientHandler(socket)).start();
            System.out.println("Connection with new client established");
        }
    }

    public void loadAccounts() {
        String line;
        try (BufferedReader fileReader = new BufferedReader(new FileReader(FILE_PATH))) {
            while ((line = fileReader.readLine()) != null) {
                userAccounts.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("No such file: " + FILE_PATH);
        } catch (IOException e) {
            System.out.println("Something went wrong");
        }
    }

    private class ClientHandler implements Runnable {
        private Message inMsg;
        private Socket socket;
        private ObjectInputStream inputStream;
        private ObjectOutputStream outputStream;
        private String username;
        private String recipient;
        private boolean loggedIn = false;

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
                        case SERVER:
                            login(inMsg.getMsg());
                            break;
                        case USER:
                            break;
                        case DISCONNECTED:
                            removeClient(inMsg.getMsg());
                            sendClientsOnline();

                    }
                } catch (EOFException e) {
                    close();
                    removeClient(this.username);
                    System.out.println("User disconnected");
                } catch (ClassNotFoundException e) {
                    close();
                    removeClient(this.username);
                    System.out.println("User disconnected");
                } catch (IOException e) {
                    close();
                    removeClient(this.username);
                    System.out.println("User disconnected");
                }
            }
        }

        private void login(String loginData) {
            if (!loggedIn) {
                try {
                    if (checkLogin(loginData)) {
                        outputStream.writeObject(new Message(MessageType.SERVER, this.username));
                        sendClientsOnline();
                    } else
                        outputStream.writeObject(new Message(MessageType.SERVER, "incorrect"));
                } catch (EOFException e) {
                    close();
                    removeClient(this.username);
                    System.out.println("User disconnected");
                } catch (IOException e) {
                    close();
                    removeClient(this.username);
                    System.out.println("User disconnected");
                }
            }
        }

        private boolean checkLogin(String loginData) throws IOException {
            for (String useraccount : userAccounts) {
                if (useraccount.equals(loginData)) {
                    StringTokenizer tokenizer = new StringTokenizer(loginData, "|");
                    username = tokenizer.nextToken();
                    if (!isLoggedIn(username)) {
                        loggedIn = true;
                        clientsOnline.add(this);
                        System.out.println("Logged in user: " + username);
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
                client.outputStream.writeObject(new Message(MessageType.SERVER, getClientsOnline()));
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

        public void close() {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (outputStream != null)
                    outputStream.close();
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Could not close streams");
            }
        }
    }
}
