package library;

import library.Interfaces.ISocketProcessor;
import java.security.PublicKey;

public class SocketProcessorEmulator implements ISocketProcessor {
    Packet tempPacket;
    boolean read = true;
    private PublicKey serverPublicKey;

    public SocketProcessorEmulator(PublicKey serverPK) {
        serverPublicKey = serverPK;
    }


    @Override
    public Packet doOperation(Packet packet) {
        tempPacket = packet;
        read = false;
        Packet response = new Packet();
        response.setFunction(packet.getFunction());
        response.setAnnouncements(packet.getAnnouncements());
        response.setUser(packet.getUser());
        response.setKey(serverPublicKey);
        response.setNumberOfAnnouncements(packet.getNumberOfAnnouncements());
        if (packet.getMessage() != null)
            response.setMessage(packet.getMessage().clone());
        response.setId(packet.getId());
        response.setUsername(packet.getUsername());
        if (packet.getMessageSignature() != null)
            response.setMessageSignature(packet.getMessageSignature().clone());

        if (packet.getUsername().equals("NOTNOT")) {
            response.setFunction(Packet.Func.USER_NOT_FOUND);
        }
        if (response.getFunction() == Packet.Func.REGISTER) {
            response.setMessage("UserAddedTest".toCharArray());
        }

        return response;
    }
}
