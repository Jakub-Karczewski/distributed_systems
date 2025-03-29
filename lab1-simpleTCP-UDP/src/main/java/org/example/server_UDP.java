package org.example;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class server_UDP extends Thread{

    public void run(){

        DatagramSocket socket1 = null;
        int portNumber = 12345;
        byte[] receiveBuffer = new byte[1024];
        System.out.println("JAVA UDP SERVER");

        try {

            socket1 = new DatagramSocket(portNumber);

            while(true) {

                Arrays.fill(receiveBuffer, (byte)0);
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket1.receive(receivePacket);
                String msg = new String(receivePacket.getData(), "UTF-8");
                String regex = "[/\n]";
                String[] spl = msg.split(regex);

                //System.out.print(Arrays.toString(spl));

                for (client_id ID : server_main.clients) {
                    if (ID.client_nr != Integer.parseInt(spl[0]) && ID.active) {
                        ID.writer.println("Client " + Integer.parseInt(spl[0]) + ":");
                        ID.writer.println(msg);
                    }
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        finally{
            if(socket1 != null){
                socket1.close();
            }
        }
    }
}
