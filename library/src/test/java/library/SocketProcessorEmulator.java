package library;

import library.Interfaces.ISocketProcessor;

public class SocketProcessorEmulator implements ISocketProcessor {
    Packet tempPacket;
    boolean read = true;

    @Override
    public Packet doOperation(Packet packet) {
        tempPacket = packet;
        read = false;
        return packet;
    }
}
