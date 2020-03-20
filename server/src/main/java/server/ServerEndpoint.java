package server;

import library.Interfaces.ICommLib;
import library.Interfaces.ISocketProcessor;
import library.Packet;

public class ServerEndpoint implements ISocketProcessor {

    private ICommLib aDPASService;

    public ServerEndpoint(ICommLib aDPASS) {
        aDPASService = aDPASS;
    }


    @Override
    public Packet doOperation(Packet packet) {
        //TODO

        if (packet.getFunction() == Packet.Func.REGISTER) {
            aDPASService.register(packet.getKey(), packet.getUsername());
        }
        return packet;
    }
}
