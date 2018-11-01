package com.alban;


import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

//entry point for server app, server main creates a socket
public class ServerName {

    public static void main(String args[]) {

        //defining port as parameter to server socket
        int port = 8818;

        try {
            //server socket,
            ServerSocket serverSocket = new ServerSocket(port);


            //this loop calls accept from the client
            while(true) {

                System.out.println("about to accept client connection..");

                /*//accept method is creating connection between server and client and this method will turn us as socket
                this socket represents the connection to client. I needed to put in this loop because I am going to continuously accept
                connections from the client*/

                Socket clientSocket = serverSocket.accept();
                System.out.println("Request accepted from " + clientSocket);

                //Created instance of ServerWorker class
                ServerWorker worker = new ServerWorker(clientSocket);

                //once the instance has created, just starting it
                worker.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
