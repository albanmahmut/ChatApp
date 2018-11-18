package com.alban;


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


/*
this class is going to be interface
to the server
*/


public class ChatClient {
    private final String ServerName;
    private final int serverPort;
    private Socket socket;
    private InputStream serverIn;
    private OutputStream serverOut;
    private BufferedReader bufferedIn;

    //for be able to register multiple user to chatclient
    private ArrayList<UserStatusListener> userStatusListeners  = new ArrayList<>();

    public ChatClient(String ServerName, int serverPort) {
        this.ServerName = ServerName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 8818);

        //login status statement
        if (!client.connect()) {
            System.err.println("Connect failed.");
        } else {
            System.out.println("Connected successful");
            if (client.login("phai", "phai")) {
                System.out.println("Login successful.");
            } else {
                System.err.println("Login failed!");

            }

        }
    }

    private boolean login(String login, String password) throws IOException {
        String cmd = "login " + login + "" + password + "\n";
        serverOut.write(cmd.getBytes());

        //handling login msg if succesful
        String response = bufferedIn.readLine();
        System.out.println("Response Line: " + response);

        if ("Login successful.".equalsIgnoreCase(response)) {
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
            //buffer reader for reading line by line
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }

    //this method for the other components to register
    public void addUserStatusListener(UserStatusListener listener) {
            userStatusListeners.add(listener);

    }
    // for remove the component
    public void  removeUserStatusListener(UserStatusListener listener) {
        userStatusListeners.remove(listener);
    }

}
