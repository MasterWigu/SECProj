package client;

import commonClasses.SRData;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientApp {

    public static void main(String[] args) {
        try {
            //args = id, faults, keystoreKey
            String arg1 = args[0];
            int id = Integer.parseInt(arg1);

            String arg2 = args[1];
            int faults = Integer.parseInt(arg2);

            String keyStoreKey = args[2];


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
                    server.setPort(Integer.parseInt(dataServers[2]));
                    servers.add(server);
                }
                reader.close();
                file.close();
                System.out.println("Read server list done");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Client c = new Client(id, servers, faults, keyStoreKey);
            c.login();
            c.work();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
