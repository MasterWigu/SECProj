package library.Algorithms;

import commonClasses.Announcement;
import commonClasses.MessageSigner;
import commonClasses.SRData;
import commonClasses.User;
import library.AuthPerfectP2PLinks;
import library.Exceptions.CommunicationErrorException;
import library.Exceptions.PacketValidationException;
import library.Packet;
import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.Arrays;
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

    public void shutdown() {
        threadPool.shutdown();
    }

    public Packet write(Packet pack) {

        Packet readWtsPack = new Packet();
        readWtsPack.setFunction(Packet.Func.GET_WTS);
        readWtsPack.setAuxFunction(pack.getFunction());
        readWtsPack.setUser(new User(-1, sender.getPubKey()));

        Packet readWtsPackResp = read(readWtsPack, false);

        if (readWtsPackResp == null) {
            System.out.println("Read WTS error!");
            return null;
        }
        wts = readWtsPackResp.getWts()+1;


        ackList = new ArrayList<>();
        return writeInner(pack, wts);
    }

    private Packet writeInner(Packet pack, int wts) {
        this.wts = wts;

        pack.setWts(wts);

        if (pack.getSingleAnnouncement() != null) { //if we are posting
            pack.getSingleAnnouncement().setWts(wts);
            MessageSigner.sign(pack.getSingleAnnouncement(), sender.getPrvKey());
        }
        ackList.clear();

        sendAllBroadcast(pack, Mode.WRITE);

        int quorum = (int) Math.ceil(((double)servers.size() + faults) / 2);
        while (ackList.size()+errorCount < servers.size() && errorCount < quorum) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(errorCount >= quorum || ackList.size() < quorum) {
            System.out.println("Not enough valid write responses for quorum!");
            return null;
        }

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

        //if we dont wait for all we can get concurrency errors later, sad
        while (readList.size()+errorCount < servers.size() && errorCount < quorum) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.println("here");
        }
        if(errorCount >= quorum || readList.size() < quorum) {
            System.out.println("Not enough valid read responses for quorum!");
            return null;
        }



        Packet maxWtsPack = readList.get(0);

        for (Packet p : readList) {
            if (p.getWts()>maxWtsPack.getWts()) {
                maxWtsPack = p;
            }
        }

        if (maxWtsPack.getAnnouncements() != null) {
            Map<Integer, ArrayList<Announcement>> tempAnns = maxWtsPack.getAnnouncements();
            for (Packet p : readList) {
                if (p.getWts() == maxWtsPack.getWts() && tempAnns != null) {
                    for (Integer key : p.getAnnouncements().keySet()) {
                        if (!tempAnns.containsKey(key)) {
                            tempAnns.put(key, new ArrayList<>());
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
            wbPack.setAuxFunction(pack.getFunction());

            wbPack.setAnnouncements(maxWtsPack.getAnnouncements());
            wbPack.setWts(maxWtsPack.getWts());

            Packet wbResp = writeInner(wbPack, wbPack.getWts());
            if (wbResp == null) {
                System.out.println("Error on writeback");
                return null;
            }
        }

        //TODO remove
        System.out.println("AAAAAA NO");
        System.out.println(maxWtsPack);
        return maxWtsPack;
    }

    private void sendAllBroadcast(final Packet pack, final Mode mode) {
        errorCount = 0;

        for (final SRData server : servers) {
            threadPool.submit(new Callable<Void>() {
                @Override
                public Void call(){
                    Packet respPack;
                    Packet packToSend = SerializationUtils.clone(pack); //Deep copy of pack for each thread
                    packToSend.setNonce(); //make sure to create new nonce so every packet is different
                    try {
                        respPack = APP2PLink.sendFunction(packToSend, sender, server);

                        if (mode == Mode.WRITE)
                            receiveWriteResp(respPack);
                        else if (mode == Mode.READ)
                            receiveReadResp(respPack, packToSend);
                    } catch (CommunicationErrorException | PacketValidationException e) {
                        errorCount++;
                    }
                    return null;
                }
            });
        }
    }

    private synchronized void receiveWriteResp(Packet pack) {
        if(wts == pack.getWts() || pack.getWts() == -10){
            ackList.add(pack);
            return;
        }
        errorCount++;
    }

    private synchronized void receiveReadResp(Packet pack, Packet sentPack) {
        //TODO remove fuck
        System.out.println(pack.getRid());
        System.out.println(rid);
        if (rid != pack.getRid()) {
            System.out.println("FUCKFUCK!");
            errorCount++;
            return;
        }

        if (pack.getFunction().equals(Packet.Func.ERROR) || pack.getFunction().equals(Packet.Func.INVALID_ANN)) {
            System.out.println("FUCK1!");
            errorCount++;
            return;
        }
        else {
            System.out.println("FUCKFUCK1!");
            //Verify all announcements inside packet
            if (pack.getAnnouncements() != null) { // if read or read_general
                System.out.println("BLAH1!");
                for (List<Announcement> anns : pack.getAnnouncements().values()) {
                    System.out.println("BLAH2!");
                    for (Announcement ann : anns) {
                        System.out.println("BLAH3!");
                        if (sentPack.getUser() != null) { // if read
                            System.out.println("BLAH4!");
                            if (!MessageSigner.verify(ann, sentPack.getUser().getPk()) || ann.getBoard() !=0) {
                                System.out.println("FUCK2!");
                                errorCount++;
                                return;
                            }
                        } else { // if read_general
                            System.out.println("BLAH5!");
                            if (!MessageSigner.verify(ann, null) || ann.getBoard() != 1) {
                                System.out.println("FUCK3!");
                                errorCount++;
                                return;
                            }
                        }
                    }
                }
            }
            if (pack.getSingleAnnouncement() != null) { // if get_ann_by_id and ann found
                System.out.println("FUCKFUCK4");
                if (!MessageSigner.verify(pack.getSingleAnnouncement(), null)) {
                    System.out.println("FUCK14");
                    errorCount++;
                    return;
                }
                if (sentPack.getCharId() != null && !Arrays.equals(pack.getSingleAnnouncement().getId(), sentPack.getCharId())) {
                    System.out.println("FUCK5!");
                    errorCount++;
                    return;
                }
            }
            if (pack.getUser() != null && sentPack.getId() != -1 && pack.getFunction() == Packet.Func.GET_USER_ID) { // if get_user_by_id and user found
                System.out.println("FUCKFUCK6");
                if (pack.getUser().getId() != sentPack.getId()) {
                    System.out.println("FUCK6!");
                    errorCount++;
                    return;
                }
            }
        }
        System.out.println("FUCKFUCKFUCKFUCKFUCK");
        System.out.println(pack);
        readList.add(pack);
    }
}


