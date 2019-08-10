package com.mypackage.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    static HashMap<String, ClientHandler> usersOnline = new HashMap<>();
    static ArrayList<String> userAccounts = new ArrayList<>();
    private final int SERVER_PORT = 5000;
    private final String FILE_PATH = "useraccounts.txt"; //TODO database instead of text file
    private ServerSocket serverSocket;

    private Server() throws IOException {
        serverSocket = new ServerSocket(SERVER_PORT);
        System.out.println("Server is running");
        loadAccounts();
        connectClient();
    }

    public static void main(String[] args) {
        try {
            new Server();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectClient() throws IOException {
        while (true) {
            System.out.println("Waiting for user to connect");
            new Thread(new ClientHandler(serverSocket.accept())).start();
            System.out.println("User connected");
        }
    }

    private void loadAccounts() {
        String line;
        try (BufferedReader fileReader = new BufferedReader(new FileReader(FILE_PATH))) {

            while ((line = fileReader.readLine()) != null) {
                userAccounts.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
