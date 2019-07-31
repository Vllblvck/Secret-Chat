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
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
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
            System.out.println("Connection with new client established");

            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            createHandler(inputStream, outputStream);
        }
    }

    public void createHandler(DataInputStream inputStream, DataOutputStream outputStream) {
        System.out.println("Creating new handler for client: ");
        ClientHandler clientHandler = new ClientHandler(socket, inputStream, outputStream);
        Thread thread = new Thread(clientHandler);
        thread.start();
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
        private DataInputStream inputStream;
        private DataOutputStream outputStream;
        private String username;
        private boolean isLoggedIn;
        private String recipient;
        private Socket socket;
        private boolean running;

        public ClientHandler(Socket socket, DataInputStream inputStream, DataOutputStream outputStream) {
            this.inputStream = inputStream;
            this.outputStream = outputStream;
            this.socket = socket;
            this.isLoggedIn = false;
            running = true;
        }

        public void closeStreams() {
            try {
                inputStream.close();
                outputStream.close();
                socket.close();
                running = false;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Could not close streams");
            }
        }

        @Override
        public void run() {
            while (running) {

                if (!isLoggedIn) {
                    try {
                        if (checkLogin(inputStream.readUTF())) {
                            outputStream.writeUTF("logged in");
                            outputStream.writeUTF(this.username);
                            outputStream.writeUTF(clientsOnline(/*this.username*/));
                        }

                    } catch (EOFException e) {
                        System.out.println("User disconnected");
                        closeStreams();
                    } catch (IOException e) {
                        e.printStackTrace();
                        closeStreams();
                        System.out.println("Something went wrong with logging in");
                    }
                }

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
                        isLoggedIn = false;
                        removeClient(this.username);
                    } catch (IOException e) {
                        isLoggedIn = false;
                        removeClient(this.username);
                    }
                }
            }
        }

        private boolean checkLogin(String loginData) {
            for (String user : userAccounts) {
                if (user.equals(loginData)) {
                    StringTokenizer tokenizer = new StringTokenizer(loginData, "|");
                    username = tokenizer.nextToken();
                    isLoggedIn = true;
                    clientsOnline.add(this);
                    System.out.println("Logged in user: " + username);
                    return true;
                }
            }
            return false;
        }

        private String clientsOnline(/*String excludedName*/) {
            StringBuffer clientsString = new StringBuffer();

            for (ClientHandler client : clientsOnline) {
                /*if (client.username.equals(excludedName))
                    continue;*/
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
    }
}
