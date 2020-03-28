package library;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

public class SocketClient {
    private String host;
    private int port;
    private PublicKey serverPublicKey;

    public SocketClient(String h, int p, PublicKey pk) {
        serverPublicKey = pk;
        host = h;
        port = p;
    }

    public Packet sendFunction(Packet message, PrivateKey pk) {
        try {
            if (host == null || port == 0 || message == null) {
                System.out.println("Invalid arguments");
                return null;
                //throw new IllegalArgumentException();
            }

            message = PacketSigner.sign(message, pk);
            Socket sendSocket = new Socket(host, port);

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


            if (!PacketSigner.verify(response)) {
                System.out.println("Message verify error!");
                return null;
            }

            return response;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    return null;

    }

}
