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
    private ServerSocket serverSocket;

    private Server() throws IOException {
        serverSocket = new ServerSocket(5000);
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
        //TODO database instead of text file
        String filePath = "useraccounts.txt";
        try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) {

            while ((line = fileReader.readLine()) != null) {
                userAccounts.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
