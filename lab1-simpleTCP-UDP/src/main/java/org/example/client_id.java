package org.example;

import java.io.PrintWriter;
import java.net.Socket;

public class client_id {
    public PrintWriter writer;
    public boolean active = true;
    public int client_nr;
    public client_id(PrintWriter wr, int nr){
        this.writer = wr;
        this.client_nr = nr;
    }
}
