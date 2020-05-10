package library;

import commonClasses.SRData;
import library.Interfaces.ISocketProcessor;

import java.security.PrivateKey;
import java.security.PublicKey;

public class SocketProcessorEmulator implements ISocketProcessor {
    Packet tempPacket;
    boolean read = true;
    private PublicKey serverPublicKey;
    private PrivateKey serverPrivateKey;

    public SocketProcessorEmulator(SRData server) {
        serverPublicKey = server.getPubKey();
        serverPrivateKey = server.getPrvKey();
    }

    @Override
    public Packet doOperation(Packet packet) {
        tempPacket = packet;
        read = false;
        Packet response = new Packet();
        response.setFunction(packet.getFunction());
        response.setAuxFunction(packet.getAuxFunction());
        response.setAnnouncements(packet.getAnnouncements());
        response.setUser(packet.getUser());

        if (packet.getMessage() != null)
            response.setMessage(packet.getMessage().clone());
        response.setId(packet.getId());
        if (packet.getCharId() != null)
            response.setCharId(packet.getCharId().clone());
        response.setSingleAnnouncement(packet.getSingleAnnouncement());


        try { // Needed to ensure that the received and sent packets have different timestamps
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return response;
    }
}
