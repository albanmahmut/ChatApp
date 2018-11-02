package com.alban;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
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

        //reading data from the client and sending data back to client
        InputStream inputStream = clientSocket.getInputStream();
        OutputStream outputStream = clientSocket.getOutputStream();

        //adding buffer reader so we can read line by line
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        //in this reader loop, going to read each line.
        while ( (line = reader.readLine()) != null) {


            //token splitting, splitting lines
            String[] tokens = StringUtils.split(line);
            //keeping reading the lines
            if ("quit".equalsIgnoreCase(line)) {
                break;
            }
            //if its not quit, echo back whatever we see from the client
            String msg = "Typed " + line + "\n";
            outputStream.write(msg.getBytes());
        }
        clientSocket.close();
    }
}
