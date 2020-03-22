package library;

import library.Interfaces.ISocketProcessor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.PrivateKey;

public class SocketServer {

    private PrivateKey serverPrivateKey;
    private ServerSocket serverSocket;
    private ISocketProcessor processor;
    private int port;
    private boolean working;

    public SocketServer(ISocketProcessor pr, int p, PrivateKey pk) {
        processor = pr;
        port = p;
        working = false;
        serverPrivateKey = pk;
    }

    public void createWorker() {
        Runnable r = new SocketServerWorker(this);
        new Thread(r).start();
    }



    void start() {
        try {
            serverSocket = new ServerSocket(port);

        } catch (IOException e) {
            e.printStackTrace();
        }
        working = true;

        while (working) {
            listenWork();
        }
    }

    public void stop() {

        if (working) {
            working = false;
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void listenWork() {
        try {
            Socket tempSocket = serverSocket.accept();
            ObjectOutputStream out = new ObjectOutputStream(tempSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(tempSocket.getInputStream());

            Packet request = (Packet) in.readObject();
            if (!PacketSigner.verify(request)) {
                System.out.println("Error verifying packet!");
                return;
            }


            Packet response = processor.doOperation(request);
            response = PacketSigner.sign(response, serverPrivateKey);

            out.writeObject(response);
            tempSocket.close();

        } catch (SocketException e) {
            System.out.println("Socket closed.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
