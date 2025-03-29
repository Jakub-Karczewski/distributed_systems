package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class server_main {
    public static ArrayList<client_id> clients = new ArrayList<>();

    public static void main(String args[]) throws IOException {

        ServerSocket serversocket = null;
        int port_ = 12345;
        int serverID = 100;

        server_UDP udp1 = new server_UDP();
        udp1.start();

        try {
            serversocket = new ServerSocket(port_);

            while (true) {

                Socket clientsocket = serversocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientsocket.getOutputStream(), true);

                String msg = in.readLine();
                System.out.println(msg);

                String regex = "[,\\s.]";
                String[] arr = msg.split(regex);
                int cid = Integer.parseInt(arr[1]);
                cid += 1;

                if(Objects.equals(arr[0], "SYN")){
                    out.println("SYN " + serverID + " + ACK " + cid);
                    msg = in.readLine();

                    if(!Objects.equals(msg, "ACK " + String.valueOf(serverID + 1))){
                        return;
                    }
                }
                else{
                    return;
                }

                clients.add(new client_id(out, cid-1));
                server_threads client_i = new server_threads(in, cid-1);
                client_i.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serversocket != null) {
                serversocket.close();
            }
        }
    }
}
