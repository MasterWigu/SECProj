package library;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketClient {
    private String host;
    private int port;

    public SocketClient(String h, int p) {
        host = h;
        port = p;
    }

    public Packet sendFunction(Packet message) {
        try {
            Socket sendSocket = new Socket(host, port);

            if (host == null || port == 0 || message == null) {
                System.out.println("Invalid arguments");
                //throw new IllegalArgumentException();
            }

            ObjectOutputStream outStream = null;
            ObjectInputStream inputStream = null;
            Packet response;


            //Send
            outStream = new ObjectOutputStream(sendSocket.getOutputStream());
            outStream.writeObject(message);

            //Receive
            inputStream = new ObjectInputStream(sendSocket.getInputStream());
            response = (Packet) inputStream.readObject();

            outStream.close();
            inputStream.close();

            sendSocket.close();

            return response;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    return null;

    }

}
