package server;


import commonClasses.KeyLoader;
import library.Interfaces.ICommLib;
import library.Interfaces.ISocketProcessor;
import library.ServerEndpoint;
import library.SocketServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.security.PublicKey;


public class DPASServer {
    public static void main(String[] args){
        int registryPort = 10250;
        System.out.println("Main OK");
        String keyStorePass = "DPASsecServer";
        String resourcesPath = "src\\main\\resources\\";
        try{
            PrivateKey serverPrivKey = KeyLoader.getPrivateKey(resourcesPath+"KeysServer", keyStorePass);
            PublicKey serverPubKey = KeyLoader.getServerPublicKey(resourcesPath+"KeysServer", keyStorePass);

            ICommLib aDPASService = new DPASService();

            ISocketProcessor processor = new ServerEndpoint(aDPASService, serverPubKey);

            SocketServer serverListener = new SocketServer(processor, registryPort, serverPrivKey);
            serverListener.createWorker();

            BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in));
            System.out.println("press <enter> to shutdown");
            reader.readLine();

            serverListener.stop();

        }catch(Exception e) {
            e.printStackTrace();
            System.out.println("DPAS server main " + e.getMessage());
        }
    }
}