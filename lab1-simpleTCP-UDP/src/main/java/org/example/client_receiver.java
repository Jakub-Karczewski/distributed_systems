package org.example;

import java.io.BufferedReader;
import java.io.IOException;

public class client_receiver extends Thread{
    private BufferedReader client_in;
    private int client_id;
    private boolean running = true;
    public client_receiver(BufferedReader in, int cid){
        this.client_in = in;
        this.client_id = cid;
    }
    public void run(){
        while(this.running){
            String read = null;
            try {
                for(int i = 0; i < 2; i++){
                    read = client_in.readLine();
                    System.out.println(read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
