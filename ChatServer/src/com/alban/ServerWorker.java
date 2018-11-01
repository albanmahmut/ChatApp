package com.alban;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;


//thread, when it runs calls handle client socket  and then communicate with the client
public class ServerWorker extends Thread {

    private final Socket clientSocket;

    public ServerWorker(Socket clientSocket) {

        this.clientSocket = clientSocket;
    }

    //every thread has a run method, this run method will whaat handle socket does
    @Override
    public void run(){
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
