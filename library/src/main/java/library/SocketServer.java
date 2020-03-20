package library;

import library.Interfaces.ISocketProcessor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

    private ServerSocket serverSocket;
    private ISocketProcessor processor;
    private int port;
    private boolean working;

    public SocketServer(ISocketProcessor pr, int p) {
        processor = pr;
        port = p;
        working = false;
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
            Packet response = processor.doOperation(request);

            out.writeObject(response);
            tempSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
