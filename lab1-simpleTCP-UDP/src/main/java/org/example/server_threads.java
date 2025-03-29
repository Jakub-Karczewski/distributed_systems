package org.example;

import java.io.BufferedReader;
import java.io.IOException;

public class server_threads extends Thread {
    public BufferedReader reader;
    private boolean running = true;
    private int client_id;

    public server_threads(BufferedReader buff, int client_id){
        reader = buff;
        this.client_id = client_id;
    }

    public void stop_run(){
        running = false;
    }

    public void run() {
        try {
            while(running) {
                String mess = reader.readLine();
                for (client_id ID : server_main.clients) {
                    if (ID.client_nr != this.client_id && ID.active) {
                        ID.writer.println("Client " + client_id + ":");
                        ID.writer.println(mess);
                    }
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
