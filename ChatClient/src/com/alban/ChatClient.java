package com.alban;


import java.io.*;
import java.net.Socket;

public class ChatClient {
    private final String ServerName;
    private final int serverPort;
    private Socket socket;
    private InputStream serverIn;
    private OutputStream serverOut;
    private BufferedReader bufferedIn;

    public ChatClient(String ServerName, int serverPort) {
        this.ServerName = ServerName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 8818);

        if (!client.connect()) {
            System.err.println("Connect failed.");
        } else {
            System.out.println("Connect successful");
            if (client.login("phai", "phai")) {
                System.out.println("Login successful...");
            } else {
                System.err.println("Login failed!");

            }


        }
    }

    private boolean login(String login, String password) throws IOException {
        String cmd = "login " + login + "" + password + "\n";
        serverOut.write(cmd.getBytes());

        String response = bufferedIn.readLine();
        System.out.println("Response Line: " + response);

        if ("login successfully".equalsIgnoreCase(response)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean connect() {
        try {
            this.socket = new Socket(ServerName, serverPort);
            System.out.println("Client port is: " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }

}
