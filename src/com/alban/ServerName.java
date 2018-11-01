package com.alban;


import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

//entry point for server app
public class ServerName {

    public static void main(String args[]) {

        //defining port as parameter to server socket
        int port = 8818;

        try {
            //server socket,
            ServerSocket serverSocket = new ServerSocket(port);


            //
            while(true) {

                /*//accept method is creating connection between server and client and this method will turn us as socket
                this socket represents the connection to client. I needed to put in this loop because I am going to continuously accept
                connections from the client*/
                Socket clientSocket = serverSocket.accept();

                //Every Soocket has a output stream, printed out stream to this ooutput stream
                OutputStream outputStream = ((Socket) clientSocket).getOutputStream();
                outputStream.write("Hello World\n".getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
