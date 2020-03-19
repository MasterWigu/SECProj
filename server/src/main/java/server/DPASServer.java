package server;

import library.Interfaces.ICommLib;
import library.Interfaces.ISocketProcessor;
import library.SocketServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class DPASServer {
    public static void main(String args[]){
        int registryPort = 8000;
        System.out.println("Main OK");
        try{
            ICommLib aDPASService = new DPASService();

            ISocketProcessor processor = new ServerEndpoint(aDPASService);

            SocketServer serverListener = new SocketServer(processor, registryPort);
            serverListener.createWorker();

            BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in));
            System.out.println("press <enter> to shutdown");
            reader.readLine();

            serverListener.stop();

        }catch(Exception e) {
            System.out.println("DPAS server main " + e.getMessage());
        }
    }
}