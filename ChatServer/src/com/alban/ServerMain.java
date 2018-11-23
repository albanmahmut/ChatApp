
package com.alban;


//entry point for server app, server main creates a socket
public class ServerMain {
    public static void main(String[] args) {

        //defining port as parameter to server socket
        int port = 8818;

        //created instance of a server class for passing in this port
        Server server = new Server(port);

        //using start will kick off the Thread
        server.start();
    }
}

