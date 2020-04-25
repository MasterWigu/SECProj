package library;

import commonClasses.SRData;
import library.Algorithms.BAtomicRegister;
import library.Exceptions.CommunicationErrorException;
import library.Exceptions.PacketValidationException;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleBroadcast {
    public static void broadcast(final Packet pack, final SRData client, ArrayList<SRData> servers){
        ExecutorService threadPool = Executors.newFixedThreadPool(servers.size());
        for (final SRData server : servers) {
            threadPool.submit(new Callable<Void>() {
                @Override
                public Void call(){
                    Packet respPack;
                    try {
                        respPack = AuthPerfectP2PLinks.sendFunction(pack, client, server);
                    } catch (CommunicationErrorException | PacketValidationException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
        }
    }



}
