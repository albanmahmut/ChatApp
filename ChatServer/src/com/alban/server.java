package com.alban;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class server extends Thread {

    private final int serverPort;

    private ArrayList < ServerWorker > workerList = new ArrayList < ServerWorker > ();

    //creating constructor
    public server(int serverPort) {

        this.serverPort = serverPort;
    }

    //serverworkers to access all other serverworkers
    public List < ServerWorker > getWorkerList() {
        return workerList;
    }


 /*The reason why I did is because I want to collection of these workers. So
 I can actually have other workers iterate through the collection of workers.
 That's how I am going to send messages to other connections. From one to another.*/

    //every thread needs to override the run method
    @Override
    public void run() {

        try {
            //server socket,
            ServerSocket serverSocket = new ServerSocket(serverPort);

            //this loop calls accept from the client
            while (true) {

                System.out.println("About to accept client connection..");

    /*//accept method is creating connection between server and client and this method will turn us as socket
    this socket represents the connection to client. I needed to put in this loop because I am going to continuously accept
    connections from the client*/

                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection request accepted from " + clientSocket);

                //Created instance of ServerWorker class
                ServerWorker worker = new ServerWorker(this, clientSocket);

                //adding worker to the arraylist
                workerList.add(worker);

                //once the instance has created, just starting it
                worker.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.run();
    }
}