package server;


import library.Interfaces.ICommLib;
import library.Interfaces.ISocketProcessor;
import library.SocketServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.security.PublicKey;


public class DPASServer {
    public static void main(String args[]){
        int registryPort = 10250;
        System.out.println("Main OK");
        try{
            //PrivateKey serverPk = PrivateKeyReader.get("private.pem");
            PrivateKey serverPrivKey = null;
            PublicKey serverPubKey = null;

            ICommLib aDPASService = new DPASService();

            ISocketProcessor processor = new ServerEndpoint(aDPASService, serverPubKey);

            SocketServer serverListener = new SocketServer(processor, registryPort, serverPrivKey);
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