package com.alban;

import ch.qos.logback.classic.Logger;
import org.apache.commons.lang3.StringUtils;


import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

//thread, when it runs calls handle client socket  and then communicate with the client
public class ServerWorker extends Thread {

    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;
    private HashSet<String> topicSet = new HashSet<String>();

    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }
    //every thread has a run method, this run method will whaat handle socket does
    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //I did refactor this loop to method.
    private void handleClientSocket() throws IOException, InterruptedException {

        //reading data from the client and sending data back to client
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();

        //adding buffer reader so we can read line by line
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        //in this reader loop, going to read each line.
        while ( (line = reader.readLine()) != null) {

            //token splitting, splitting lines
            String[] tokens = StringUtils.split(line);
            if (tokens != null && tokens.length > 0) {
                String cmd = tokens[0];

                //keeping reading the lines
                if ("logoff".equals(cmd) || "quit".equalsIgnoreCase(cmd)) {
                    handleLogoff();
                    break;

                    //creating function to keep loop simple
                } else if ("login".equalsIgnoreCase(cmd)) {
                    handleLogin(outputStream, tokens);

                    //handling message to sending to each other
                } else if ("msg".equalsIgnoreCase(cmd)) {
                    String[] tokensMsg = StringUtils.split(line, null, 3);
                    handleMessage(tokensMsg);
                } else if ("join".equalsIgnoreCase(cmd)) {
                    handleJoin(tokens);
                } else if ("leave".equalsIgnoreCase(cmd)) {
                    handleLeave(tokens);

                    //if its not quit, echo back whatever we see from the client
                } else {
                    String msg = "unknown " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }

        clientSocket.close();
    }

    private void handleLeave(String[] tokens) {

        //leave the topic
        if (tokens.length > 1) {
            String topic = tokens[1];

            //by areoving topic set, i say that this connection is part of the topic
            topicSet.remove(topic);
        }
    }

    //new function testing for to see if this topic is inside the topicset
    public boolean isMemberOfTopic(String topic) {
        return topicSet.contains(topic);
    }

    private void handleJoin(String[] tokens) {

        //topic
        if (tokens.length > 1) {
            String topic = tokens[1];

            //by adding topic set, i say that this connection is part of the topic
            topicSet.add(topic);
        }
    }

    // format: "msg" "login" body...
    // format: "msg" "#topic" body...
    private void handleMessage(String[] tokens) throws IOException {
        String sendTo = tokens[1];
        String body = tokens[2];

        //testing the first character (for hashtag #)
        boolean isTopic = sendTo.charAt(0) == '#';


        //iterating to list of workers if the logins match
        List<ServerWorker> workerList = server.getWorkerList();
        for(ServerWorker worker : workerList) {

            //if this is a topic
            if (isTopic) {
                if (worker.isMemberOfTopic(sendTo)) {
                    String outMsg = "msg " + sendTo + ":" + login + " " + body + "\n";
                    worker.send(outMsg);
                }
            } else {
                //if the logins matches
                if (sendTo.equalsIgnoreCase(worker.getLogin())) {

                    //if matches, sending msg
                    String outMsg = "msg " + login + " " + body + "\n";
                    worker.send(outMsg);
                }
            }
        }
    }


    //closing the current socket, sending to every other user status that current user has logged off.
    private void handleLogoff() throws IOException {

        //when logoff, removing the online user from the working list
        server.removeWorker(this);

        //sending other online users current user's status
        List<ServerWorker> workerList = server.getWorkerList();

        // send other online users current user's status
        String onlineMsg = "offline " + login + "\n";
        for(ServerWorker worker : workerList) {
            if (!login.equals(worker.getLogin())) {
                worker.send(onlineMsg);
            }
        }
        clientSocket.close();
    }

    //exposing the login so then other serverworkings knows where the login is
    public String getLogin() {

        return login;
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {

        // taking the login and password what the user is entered
        if (tokens.length == 3) {
            String login = tokens[1];
            String password = tokens[2];

            // checking and allowing to user login, in this case "guess is valid user"
            if ((login.equals("phai") && password.equals("phai")) || (login.equals("mami") && password.equals("mami")) ) {
                String msg = "ok login\n";
                outputStream.write(msg.getBytes());

                // so this login assigned to the this user (phai) in this case
                this.login = login;
                System.out.println("User logged in succesfully: " + login);

                //sending online message to every other serverworkers the current user has logged in
                List<ServerWorker> workerList = server.getWorkerList();

                // send current user all other online logins
                for(ServerWorker worker : workerList) {

                    //this statement will take care of not sending own presence.
                    if (worker.getLogin() != null) {
                        if (!login.equals(worker.getLogin())) {
                            String msg2 = "online " + worker.getLogin() + "\n";
                            send(msg2);
                        }
                    }
                }

                // send other online users current user's status
                String onlineMsg = "online " + login + "\n";
                for(ServerWorker worker : workerList) {
                    if (!login.equals(worker.getLogin())) {
                        worker.send(onlineMsg);
                    }
                }

                //if user using invalid password and username
            } else {
                String msg = "error login\n";
                outputStream.write(msg.getBytes());
                System.err.println("Login failed for " + login);
            }
        }
    }

    private void send(String msg) throws IOException {
        if (login != null) {
            try {
                outputStream.write(msg.getBytes());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

