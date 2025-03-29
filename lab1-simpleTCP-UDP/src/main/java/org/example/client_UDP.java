package org.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class client_UDP extends Thread{
    int cid;
    private int running = 0;
    private DatagramSocket socket1;
    private InetAddress adress;
    public client_UDP(int ID, int run){
        this.cid = ID;
        this.running = 1;
        try {
            this.socket1 = new DatagramSocket();
            this.adress = InetAddress.getByName("localhost");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run() {
        if(running == 1) {
            try {
                byte[] sendBuffer = buffer_reader.readAll(this.cid, "tekst.txt").getBytes();
                int portNumber = 12345;
                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, this.adress, portNumber);
                socket1.send(sendPacket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (socket1 != null) {
                    socket1.close();
                }
            }
        }
    }
}
