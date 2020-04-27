package library.Algorithms;

import commonClasses.Announcement;
import commonClasses.MessageSigner;
import library.AuthPerfectP2PLinks;
import library.Exceptions.CommunicationErrorException;
import library.Exceptions.PacketValidationException;
import library.Packet;
import commonClasses.SRData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BAtomicRegister {
    private enum Mode {
        READ,
        WRITE
    }


    private SRData sender;
    private List<SRData> servers;
    private AuthPerfectP2PLinks APP2PLink;

    private int wts;
    private int rid;
    private int errorCount;

    private List<Packet> ackList;
    private List<Packet> readList;

    private final ExecutorService threadPool;
    private int faults;



    public BAtomicRegister(SRData sender, List<SRData> servers, int faults) {
        APP2PLink = new AuthPerfectP2PLinks();
        APP2PLink.init();

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

        Packet readWtsPackResp = read(readWtsPack, false);

        wts = readWtsPackResp.getWts()+1;


        ackList = new ArrayList<>();
        return writeInner(pack, wts);
    }

    private Packet writeInner(Packet pack, int wts) {
        this.wts = wts;

        pack.setWts(wts);

        if (pack.getSingleAnnouncement() != null) { //if we are posting
            pack.getSingleAnnouncement().setWts(wts);
            pack.getSingleAnnouncement().setSignature(MessageSigner.sign(pack.getSingleAnnouncement(), sender.getPrvKey()));
        }
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
        return read(pack, true);
    }

    private Packet read(Packet pack, boolean writeBack) {
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

        if (maxWtsPack.getAnnouncements() != null) {
            Map<Integer, List<Announcement>> tempAnns = maxWtsPack.getAnnouncements();
            for (Packet p : readList) {
                if (p.getWts() == maxWtsPack.getWts() && tempAnns != null) {
                    for (Integer key : p.getAnnouncements().keySet()) {
                        if (!tempAnns.containsKey(key)) {
                            tempAnns.put(key, new ArrayList<Announcement>());
                        }
                        for (Announcement ann : p.getAnnouncements().get(key)) {
                            if (!tempAnns.get(key).contains(ann)) {
                                tempAnns.get(key).add(ann);
                            }
                        }
                    }
                }
            }
            maxWtsPack.setAnnouncements(tempAnns);
        }



        if (writeBack) {
            Packet wbPack = new Packet();
            wbPack.setFunction(Packet.Func.WRITE_BACK);

            wbPack.setAnnouncements(maxWtsPack.getAnnouncements());
            wbPack.setWts(maxWtsPack.getWts());

            Packet wbResp = writeInner(wbPack, wbPack.getWts());
            if (wbResp == null)
                System.out.println("Error on writeback");
                return null;
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
                        respPack = APP2PLink.sendFunction(pack, sender, server);

                        if (mode == Mode.WRITE)
                            receiveWriteResp(respPack);
                        else if (mode == Mode.READ)
                            receiveReadResp(respPack, pack);
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

    private synchronized void receiveReadResp(Packet pack, Packet sentPack) {
        if (rid != pack.getRid())
            return;

        if (pack.getFunction().equals(Packet.Func.ERROR) || pack.getFunction().equals(Packet.Func.INVALID_ANN)) {
            errorCount++;
            return;
        }
        else {
            //Verify all announcements inside packet
            if (pack.getAnnouncements() != null) {
                for (List<Announcement> anns : pack.getAnnouncements().values()) {
                    for (Announcement ann : anns) {
                        if (sentPack.getUser() != null) {
                            if (!MessageSigner.verify(ann, sentPack.getUser().getPk()) || ann.getBoard() !=1) {
                                errorCount++;
                                return;
                            }
                        } else {
                            if (!MessageSigner.verify(ann, null) || ann.getBoard() != 0) {
                                errorCount++;
                                return;
                            }
                        }
                    }
                }
            }
            if (pack.getSingleAnnouncement() != null) {
                if (!MessageSigner.verify(pack.getSingleAnnouncement(), null)) {
                    errorCount++;
                    return;
                }
                if (sentPack.getId() != -1 && pack.getSingleAnnouncement().getId() != sentPack.getId()) {
                    errorCount++;
                    return;
                }
            }
            if (pack.getUser() != null && sentPack.getId() != -1 && pack.getFunction() == Packet.Func.GET_USER_ID) {
                if (pack.getUser().getId() != sentPack.getId()) {
                    errorCount++;
                    return;
                }
            }
        }
        readList.add(pack);
    }

}


