package com.alban;


//entry point for server app, server main creates a socket
public class ServerName {

    public static void main(String args[]) {

        //defining port as parameter to server socket
        int port = 8818;

        //created instance of a server class for passing in this port
        server server = new server(port);

        //using start will kick off the Thread
        server.start();

    }

}