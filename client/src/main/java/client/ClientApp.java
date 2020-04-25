package client;

import commonClasses.SRData;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClientApp {

    public static void main(String[] args) {
        try {
            String b = args[0];
            int a = Integer.parseInt(b);

            ArrayList<SRData> servers = new ArrayList<>();
            try {
                //Saving of object in a file
                FileInputStream file = new FileInputStream("src\\main\\resources\\ServerList.txt");
                Scanner reader = new Scanner(file);

                while(reader.hasNextLine()){
                    String data = reader.nextLine();
                    //number;host;port
                    String[] dataServers = data.split(";", 5);
                    SRData server = new SRData();
                    server.setId(Integer.parseInt(dataServers[0]));
                    server.setHost(dataServers[1]);
                    server.setId(Integer.parseInt(dataServers[2]));
                    servers.add(server);
                }
                reader.close();
                file.close();
                System.out.println("Read server list done");
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // TODO define faults
            Client c = new Client(a, servers, 0);
            c.login();
            c.work();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
