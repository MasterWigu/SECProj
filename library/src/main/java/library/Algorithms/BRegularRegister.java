package library.Algorithms;

import commonClasses.Announcement;
import commonClasses.MessageSigner;
import library.AuthPerfectP2PLinks;
import library.Exceptions.CommunicationErrorException;
import library.Exceptions.PacketValidationException;
import library.Packet;
import library.SRData;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BRegularRegister {
    private enum Mode {
        READ,
        WRITE,
    }


    private SRData sender;
    private List<SRData> servers;

    private int wts;
    private int rid;
    private int errorCount;

    private List<Packet> ackList;
    private List<Packet> readList;

    private final ExecutorService threadPool;
    private int faults;



    public BRegularRegister(SRData sender, List<SRData> servers, int faults) {
        wts = 0;
        rid = 0;

        ackList = new ArrayList<>();
        readList = new ArrayList<>();
        this.sender = sender;
        this.servers = servers;

        this.threadPool = Executors.newFixedThreadPool(servers.size());
        this.faults = faults;
    }

    public Packet write(Packet pack) {

        Packet readWtsPack = new Packet();
        readWtsPack.setFunction(Packet.Func.GET_WTS);
        readWtsPack.setSenderPk(sender.getPubKey());

        Packet readWtsPackResp = read(readWtsPack);

        wts = readWtsPackResp.getWts()+1;


        ackList = new ArrayList<>();
        return writeInner(pack, wts);
    }

    private Packet writeInner(Packet pack, int wts) {
        this.wts = wts;

        pack.setWts(wts);

        ackList.clear();

        sendAllBroadcast(pack, Mode.WRITE);

        int quorum = (int) Math.ceil(((double)servers.size() + faults) / 2);
        while (ackList.size() < quorum && errorCount < quorum) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(errorCount >= quorum)
            return null;

        return ackList.get(0);
    }



    public Packet read(Packet pack) {
        rid++;
        pack.setRid(rid);

        readList.clear();

        sendAllBroadcast(pack, Mode.READ);

        int quorum = (int) Math.ceil(((double)servers.size() + faults) / 2);
        while (readList.size() < quorum && errorCount < quorum) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(errorCount >= quorum)
            return null;

        Packet maxWtsPack = readList.get(0);
        for (Packet p : readList) {
            if (p.getWts()>maxWtsPack.getWts()) {
                maxWtsPack = p;
            }
        }

        return maxWtsPack;
    }

    private void sendAllBroadcast(final Packet pack, final Mode mode) {
        errorCount = 0;

        for (final SRData server : servers) {

            threadPool.submit(new Callable<Void>() {
                @Override
                public Void call(){

                    Packet respPack;
                    try {
                        respPack = AuthPerfectP2PLinks.sendFunction(pack, sender, server);

                        if (mode == Mode.WRITE)
                            receiveWriteResp(respPack);
                        else if (mode == Mode.READ)
                            receiveReadResp(respPack);
                    } catch (CommunicationErrorException | PacketValidationException e) {
                        errorCount++;
                    }




                    return null;
                }
            });
        }
    }

    private synchronized void receiveWriteResp(Packet pack) {
        if(wts == pack.getWts())
            ackList.add(pack);
    }

    private synchronized void receiveReadResp(Packet pack) {
        if (rid != pack.getRid())
            return;

        if (pack.getFunction().equals(Packet.Func.ERROR) || pack.getFunction().equals(Packet.Func.INVALID_ANN)
                || pack.getFunction().equals(Packet.Func.USER_NOT_FOUND)) {
            errorCount++;
            return;
        }
        else {
            //Verify all announcements inside packet
            if (pack.getAnnouncements() != null) {
                for (Announcement ann : pack.getAnnouncements()) {
                    if (!MessageSigner.verify(ann)) {
                        errorCount++;
                        return;
                    }
                }
            }
        }

        readList.add(pack);

    }

}


