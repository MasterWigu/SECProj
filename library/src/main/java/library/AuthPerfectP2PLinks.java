package library;

import commonClasses.SRData;
import library.Exceptions.CommunicationErrorException;
import library.Exceptions.PacketValidationException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// Antigo SocketClient
public class AuthPerfectP2PLinks {
    private static List<Integer> delivered;

    public void init() {
        delivered = new ArrayList<>();
    }

    public Packet sendFunction(Packet message, SRData sender, SRData receiver) throws PacketValidationException, CommunicationErrorException {
        try {
            if (receiver.getHost() == null || receiver.getPort() == 0 || message == null) {
                System.out.println("Invalid arguments");
                return null;
                //throw new IllegalArgumentException();
            }

            message.setSenderPk(sender.getPubKey());
            message.setReceiverPk(receiver.getPubKey());
            PacketSigner.sign(message, sender.getPrvKey());

            Socket sendSocket = new Socket(receiver.getHost(), receiver.getPort());

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

            if (!getFresh(response)) {
                System.out.println("Freshness error!");
                throw new PacketValidationException();
            }

            if (response.getSenderPk() == null || response.getReceiverPk() == null ||
                    !response.getSenderPk().equals(receiver.getPubKey()) || !response.getReceiverPk().equals(sender.getPubKey())) {
                System.out.println("Pair sender/receiver invalid!");
                throw new PacketValidationException();
            }

            if (!PacketSigner.verify(response, receiver.getPubKey())) {
                System.out.println("Message verify error!");
                throw new PacketValidationException();
            }

            return response;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new CommunicationErrorException();
        }
    }

    private static boolean getFresh(Packet pack) {
        Integer nonce = pack.getNonce();
        //Check freshness
        if(delivered.contains(nonce))
            return false;
        delivered.add(nonce);
        return true;
    }

}
