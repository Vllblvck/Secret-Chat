package com.mypackage.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatServer {
    private static AtomicBoolean serverIsRunning = new AtomicBoolean();
    private final int SERVER_PORT = 5000;
    private final String FILE_PATH = "useraccounts.txt";
    private Vector<ClientHandler> clientsOnline = new Vector<>();
    private ServerSocket serverSocket;
    private Socket socket;
    private ArrayList<String> userAccounts = new ArrayList<>();

    public ChatServer() throws IOException {
        serverSocket = new ServerSocket(SERVER_PORT);
        serverIsRunning.set(true);
        loadAccounts();
        connectClient();
    }

    public static void main(String[] args) {
        try {
            new ChatServer();
        } catch (IOException e) {
            System.out.println("Failed to run server");
            serverIsRunning.set(false);
            e.printStackTrace();
        }
    }

    public void connectClient() throws IOException {
        while (serverIsRunning.get()) {
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
        private Socket socket;
        private DataInputStream inputStream;
        private DataOutputStream outputStream;
        private String username;
        private String recipient;
        private boolean isLoggedIn = false;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            while (!socket.isClosed()) {
                login();
                //handleMessages();
                //TODO ogarnij jak aktualizowac liste userow online
                sendUsersOnline();
            }
        }

        private void sendUsersOnline() {
            try {
                outputStream.writeUTF(getClientsOnline(this.username));
            } catch (IOException e) {
                close();
                removeClient(this.username);
                System.out.println("User disconnected");
            }
        }

        private void login() {
            if (!isLoggedIn) {
                try {
                    if (checkLogin(inputStream.readUTF())) {
                        outputStream.writeUTF("logged in");
                        outputStream.writeUTF(this.username);
                        outputStream.writeUTF(getClientsOnline(this.username));
                    } else
                        outputStream.writeUTF("not logged in");
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

        private boolean checkLogin(String loginData) {
            for (String useraccount : userAccounts) {
                if (useraccount.equals(loginData)) {
                    StringTokenizer tokenizer = new StringTokenizer(loginData, "|");
                    username = tokenizer.nextToken();
                    if (!ifLoggedIn(username)) {
                        isLoggedIn = true;
                        clientsOnline.add(this);
                        System.out.println("Logged in user: " + username);
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean ifLoggedIn(String username) {
            for (ClientHandler client : clientsOnline) {
                if (client.username.equals(username))
                    return true;
            }
            return false;
        }

        private void handleMessages() {
            if (isLoggedIn) {
                String receivedMsg;

                try {
                    receivedMsg = inputStream.readUTF();
                    System.out.println(receivedMsg);

                    if (recipient != null) {
                        for (ClientHandler client : clientsOnline) {
                            if (client.username.equals(recipient) && client.isLoggedIn == true) {
                                client.outputStream.writeUTF(this.username + " : " + receivedMsg);
                                break;
                            }
                        }
                    }

                } catch (SocketException e) {
                    close();
                    isLoggedIn = false;
                    removeClient(this.username);
                    System.out.println("User disconnected");
                } catch (IOException e) {
                    close();
                    isLoggedIn = false;
                    removeClient(this.username);
                    System.out.println("User disconnected");
                }
            }
        }

        private String getClientsOnline(String excludedName) {
            StringBuffer clientsString = new StringBuffer();

            for (ClientHandler client : clientsOnline) {
                if (client.username.equals(excludedName))
                    continue;
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
                inputStream.close();
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Could not close streams");
            }
        }
    }
}
