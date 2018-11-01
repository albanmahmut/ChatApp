package com.alban;


import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

//entry point for server app
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

                //creating a new thread everytime when i get a new connection from the client
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            handleClientSocket(clientSocket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //I did refactor this loop to method.
    private static void handleClientSocket(Socket clientSocket) throws IOException, InterruptedException {

        //Every Soocket has a output stream, printed out stream to this ooutput stream
        OutputStream outputStream = clientSocket.getOutputStream();


        //this loop set limits to 10 second, during this 10 seconds no other client can connect server
        for (int i = 0; i < 10; i++) {
            outputStream.write(("Time is " + new Date() + "\n").getBytes());
            Thread.sleep(1000);

        }
        clientSocket.close();
    }
}
