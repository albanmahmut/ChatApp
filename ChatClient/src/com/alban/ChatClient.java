package com.alban;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/*
this class is going to be interface
to the server
*/

public class ChatClient {
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private InputStream serverIn;
    private OutputStream serverOut;
    private BufferedReader bufferedIn;

    //for be able to register multiple user to chatclient
    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();

    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 8818);

        //added user listener and getting callback for who is online
        client.addUserStatusListener(new UserStatusListener() {
            @Override
            public void online(String login) {
                System.out.println("ONLINE: " + login);
            }

            @Override
            public void offline(String login) {
                System.out.println("OFFLINE: " + login);
            }
        });

        client.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(String fromLogin, String msgBody) {
                System.out.println("You got a message from " + fromLogin + " ===> " + msgBody);
            }
        });


        //login status statement
        if (!client.connect()) {
            System.err.println("Connect failed.");
        } else {
            System.out.println("Connect successful");


            //Created interface.. and here, before the users login I am registered all the listeners.
            if (client.login("phai", "phai")) {
                System.out.println("Login successful");

                client.msg("mami", "Hello World!");
            } else {
                System.err.println("Login failed");
            }

            //client.logoff();
        }
    }

    public void msg(String sendTo, String msgBody) throws IOException {
        String cmd = "msg " + sendTo + " " + msgBody + "\n";
        serverOut.write(cmd.getBytes());
    }

    public boolean login(String login, String password) throws IOException {
        String cmd = "login " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());

        //handling login msg if succesful
        String response = bufferedIn.readLine();
        System.out.println("Response Line:" + response);

        if ("ok login".equalsIgnoreCase(response)) {

            //once the user logged in, then going to start to recieve event from the server.
            //Basically, reading responses from the server
            startMessageReader();
            return true;
        } else {
            return false;
        }
    }

    //its going to send a logoff command to the server
    public void logoff() throws IOException {
        String cmd = "logoff\n";
        serverOut.write(cmd.getBytes());
    }
    //creating a new thread and going to execute read message loop
    private void startMessageReader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        t.start();
    }

    private void readMessageLoop() {
        try {
            String line;

            //infinite loop that reading line from the server output
            while ((line = bufferedIn.readLine()) != null) {

                //token splitting, splitting lines
                String[] tokens = StringUtils.split(line);
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];

                    //handling online and offline presence messages
                    if ("online".equalsIgnoreCase(cmd)) {
                        handleOnline(tokens);

                        //handling offline msg
                    } else if ("offline".equalsIgnoreCase(cmd)) {
                        handleOffline(tokens);
                    } else if ("msg".equalsIgnoreCase(cmd)) {
                        String[] tokensMsg = StringUtils.split(line, null, 3);
                        handleMessage(tokensMsg);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMessage(String[] tokensMsg) {
        String login = tokensMsg[1];
        String msgBody = tokensMsg[2];

        //calling all the message listeners
        for(MessageListener listener : messageListeners) {
            listener.onMessage(login, msgBody);
        }
    }

    //call the all registered user status listeners
    private void handleOffline(String[] tokens) {
        String login = tokens[1];

        //handling offline msg
        for(UserStatusListener listener : userStatusListeners) {
            listener.offline(login);
        }
    }

    //call the all registered user status listeners
    private void handleOnline(String[] tokens) {
        String login = tokens[1];

        //handling online msg
        for(UserStatusListener listener : userStatusListeners) {
            listener.online(login);
        }
    }

    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
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
    public void removeUserStatusListener(UserStatusListener listener) {

        userStatusListeners.remove(listener);
    }

    public void addMessageListener(MessageListener listener) {

        messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener) {

        messageListeners.remove(listener);
    }

}
