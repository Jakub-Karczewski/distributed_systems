package org.example;

import java.io.*;
import java.util.Scanner;

public class buffer_reader {
    public static String readAll(int client_id, String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        sb.append(client_id).append("\n");
        String path_ = "C:\\Users\\HP\\IdeaProjects\\rozprochy_lab1\\src\\main\\java\\org\\example\\";

        try {
            File myObj = new File(path_ + filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                line = myReader.nextLine();
                sb.append(line).append("\n");
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
