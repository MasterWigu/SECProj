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
        String arg1 = args[0];
        int id = Integer.parseInt(arg1);

        String keyStorePass = args[1];

        int registryPort = 10250+id;
        System.out.println("Main OK");
        String resourcesPath = "src\\main\\resources\\";

        try{
            PrivateKey serverPrivKey = KeyLoader.getPrivateKey(resourcesPath+"KeysServer"+id, keyStorePass);
            PublicKey serverPubKey = KeyLoader.getServerPublicKey(resourcesPath+"KeysServer"+id, id, keyStorePass);

            ICommLib aDPASService = new DPASService(id);

            ISocketProcessor processor = new ServerEndpoint(aDPASService);

            SocketServer serverListener = new SocketServer(processor, registryPort, serverPrivKey, serverPubKey);
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