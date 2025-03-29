package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;

public class client_main {

    public static void main(String args[]) throws IOException {

        String inp;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int myID = Integer.parseInt(args[0]);
        int running = Integer.parseInt(args[1]);

        client_UDP udp1 = new client_UDP(myID, running);
        //udp1.start();


        System.out.println("JAVA TCP CLIENT " + myID);
        String hostName = "localhost";
        String regex = "[,\\s.]";
        int portNumber = 12345;
        Socket socket = null;

        try {

            socket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("SYN " + myID);
            String[] confirm = in.readLine().split(regex);

            if(!Objects.equals(confirm[0], "SYN") && Objects.equals(confirm[3], "ACK") && Integer.parseInt(confirm[4]) == myID + 1){
                return;
            }
            out.println("ACK " + (Integer.parseInt(confirm[1]) + 1));
            Thread.sleep(200);
            client_receiver rcv = new client_receiver(in, Integer.parseInt(args[0]));
            rcv.start();


            while(true){
                inp = br.readLine();
                if(Objects.equals(inp, "-U")){
                    udp1.start();
                }
                else {
                    out.println(inp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null){
                socket.close();
            }
        }
    }

}

