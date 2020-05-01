package library;

import library.Interfaces.ISocketProcessor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class SocketServer {

    private PrivateKey serverPrivateKey;
    private PublicKey serverPublicKey;
    private ServerSocket serverSocket;
    private ISocketProcessor processor;
    private int port;
    private boolean working;

    public SocketServer(ISocketProcessor pr, int p, PrivateKey pk, PublicKey spk) {
        processor = pr;
        port = p;
        working = false;
        serverPrivateKey = pk;
        serverPublicKey = spk;
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
        try {
            Thread.sleep(2000); //to ensure the socket is started so it can be closed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        working = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenWork() {
        try {
            Socket tempSocket = serverSocket.accept();
            ObjectOutputStream out = new ObjectOutputStream(tempSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(tempSocket.getInputStream());

            Packet request = (Packet) in.readObject();
            Packet response;
            if (!PacketSigner.verify(request, request.getSenderPk())) {
                System.out.println("Error verifying packet!");
                response = new Packet();
                response.setFunction(Packet.Func.ERROR);
                response.setSenderPk(serverPublicKey);
                response.setMessage("Invalid Packet received".toCharArray());
            }
            else {
                response = processor.doOperation(request);
            }
            response.setReceiverPk(request.getSenderPk());
            response.setSenderPk(serverPublicKey);
            PacketSigner.sign(response, serverPrivateKey);
            out.writeObject(response);
            tempSocket.close();

        } catch (SocketException e) {
            System.out.println("Socket closed.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
