package com.mypackage.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Vector;

public class ChatServer {
    protected static Vector<ClientHandler> clientsOnline = new Vector<>();
    protected static ArrayList<String> userAccounts = new ArrayList<>();
    private final int SERVER_PORT = 5000;
    private final String FILE_PATH = "useraccounts.txt";
    private ServerSocket serverSocket;

    public ChatServer() throws IOException {
        serverSocket = new ServerSocket(SERVER_PORT);
        loadAccounts();
        connectClient();
    }

    public static void main(String[] args) {
        try {
            new ChatServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectClient() throws IOException {
        while (true) {
            new Thread(new ClientHandler(serverSocket.accept())).start();
        }
    }

    private void loadAccounts() {
        String line;
        try (BufferedReader fileReader = new BufferedReader(new FileReader(FILE_PATH))) {
            while ((line = fileReader.readLine()) != null) {
                userAccounts.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
