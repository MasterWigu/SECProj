package library;

import library.Interfaces.ISocketProcessor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServerWorker implements Runnable {
    private SocketServer socketServer;

    public SocketServerWorker(SocketServer socketServer) {
        socketServer = socketServer;
    }




    public void run() {
        socketServer.start();
    }

}
