package com.alban;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.List;


//thread, when it runs calls handle client socket  and then communicate with the client
public class ServerWorker extends Thread {

    private final Socket clientSocket;
    private final server server;

    //tagging connection as user, assigning it to null if its not login
    private String login = null;
    private OutputStream outputStream;

    public ServerWorker(server server, Socket clientSocket) {
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
        while ((line = reader.readLine()) != null) {

            //token splitting, splitting lines
            String[] tokens = StringUtils.split(line);

            if (tokens != null && tokens.length > 0) {
                String cmd = tokens[0];

                //keeping reading the lines
                if ("logoff".equals(cmd) || "quit".equalsIgnoreCase(cmd)) {
                    handleLogOff();
                    break;

                    //creating function to keep loop simple
                } else if ("login".equalsIgnoreCase(cmd)) {
                    handleLogin(outputStream, tokens);

                    //if dont recognize the command then sending the error back to client
                } else {
                    String msg = "unknown" + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }

    /*  //if its not quit, echo back whatever we see from the client
      String msg = "Typed " + line + "\n";
      outputStream.write(msg.getBytes()); */
            }

        }
        clientSocket.close();
    }

    //closing the current socket, sending to every other user status that current user has logged off.
    private void handleLogOff() throws IOException {
        //sending other online users current user's status
        List < ServerWorker > workerList = server.getWorkerList();

        String onLineMsg = login + " is offline.\n";
        for (ServerWorker worker: workerList) {
            if (!login.equals(worker.getLogin())) {

                worker.send(onLineMsg);
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
            if (login.equals("phai") && password.equals("phai") || login.equals("mami") && password.equals("mami")) {
                String msg = "login successful...\n";
                outputStream.write(msg.getBytes());

                // so this login assigned to the this user (guest) in this case
                this.login = login;
                System.out.println("User is succesfully logged in: " + login);

                //sending online message to every other serverworkers the current user has logged in
                List < ServerWorker > workerList = server.getWorkerList();

                //sending current user to all other online logins
                for (ServerWorker worker: workerList) {

                    //this statement will take care of not sending own presence.
                    if (worker.getLogin() != null) {
                        if (!login.equals(worker.getLogin())) {

                            String msg2 = worker.getLogin() + " is online.\n";
                            send(msg2);
                        }
                    }
                }


                //sending other online users current user's status
                String onLineMsg = login + " is online.\n";
                for (ServerWorker worker: workerList) {
                    if (!login.equals(worker.getLogin())) {

                        worker.send(onLineMsg);
                    }
                }

                //if user using invalid password and username
            } else {
                String msg = "login error. Please try again...\n";
                outputStream.write(msg.getBytes());
            }
        }
    }

    //access to upperstream of client soocket and send message to user
    private void send(String msg) throws IOException {

        //if there is no login, i dont wanna see the messages
        if (login != null) {
            outputStream.write(msg.getBytes());
        }
    }
}