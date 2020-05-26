package library;

import commonClasses.Announcement;
import commonClasses.MessageSigner;
import commonClasses.SRData;
import commonClasses.User;
import commonClasses.exceptions.*;
import library.Interfaces.ICommLib;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DPASEmulation implements ICommLib {
    private SRData server;
    private SRData client1;
    private SRData client2;
    private SRData client3;


    DPASEmulation(SRData server, SRData client1, SRData client2, SRData client3){
        this.server = server;
        this.client1 = client1;
        this.client2 = client2;
        this.client3 = client3;
    }

    @Override
    public String register(PublicKey key, int wts, Packet p) throws KeyException, InvalidWtsException {

        if (key == null)
            throw new KeyException();
        if (wts <= 0)
            throw new InvalidWtsException();

        return "UserAddedTest";
    }

    @Override
    public synchronized String post(PublicKey pk, Announcement announcement, int wts) throws UserNotFoundException, InvalidAnnouncementException {

        if (Arrays.equals(announcement.getMessage(), "ERROR_U2".toCharArray()) && server.getId() > 2)
            throw new UserNotFoundException();
        else if (Arrays.equals(announcement.getMessage(), "ERROR_U1".toCharArray()) && server.getId() > 3)
            throw new UserNotFoundException();

        if (Arrays.equals(announcement.getMessage(), "ERROR_A2".toCharArray()) && server.getId() > 2)
            throw new InvalidAnnouncementException();
        else if (Arrays.equals(announcement.getMessage(), "ERROR_A1".toCharArray()) && server.getId() > 3)
            throw new InvalidAnnouncementException();


        return "PostedCreatedTest";
    }

    @Override
    public String postGeneral(PublicKey key, Announcement announcement, int wts) throws UserNotFoundException, InvalidAnnouncementException {

        if (Arrays.equals(announcement.getMessage(), "ERROR_U2".toCharArray()) && server.getId() > 2)
            throw new UserNotFoundException();
        else if (Arrays.equals(announcement.getMessage(), "ERROR_U1".toCharArray()) && server.getId() > 3)
            throw new UserNotFoundException();

        if (Arrays.equals(announcement.getMessage(), "ERROR_A2".toCharArray()) && server.getId() > 2)
            throw new InvalidAnnouncementException();
        else if (Arrays.equals(announcement.getMessage(), "ERROR_A1".toCharArray()) && server.getId() > 3)
            throw new InvalidAnnouncementException();

        return "PostedGeneralCreatedTest";
    }

    @Override
    public HashMap<Integer, ArrayList<Announcement>> read(PublicKey key, Packet packet) throws UserNotFoundException {

        if (key.equals(client2.getPubKey()) && server.getId() > 3) //if key from client2, 1 error
            throw new UserNotFoundException();
        else if (!key.equals(client2.getPubKey()) && !key.equals(client1.getPubKey())  && server.getId() > 2) // if key from client 3, 2 errors
            throw new UserNotFoundException();


        User u = new User(123, key);
        Announcement ann = new Announcement("READ".toCharArray(), u, null, 0);
        ann.setWts(1);
        ann.setId("P0_1".toCharArray());
        if (key.equals(client1.getPubKey()))
            MessageSigner.sign(ann, client1.getPrvKey());
        else if (key.equals(client2.getPubKey()))
            MessageSigner.sign(ann, client2.getPrvKey());
        else
            MessageSigner.sign(ann, client3.getPrvKey());

        HashMap<Integer, ArrayList<Announcement>> tempResponse = new HashMap<>();
        ArrayList<Announcement> tempA = new ArrayList<>();
        tempA.add(ann);
        tempResponse.put(-1, tempA);

        packet.setWts(1);

        return tempResponse;
    }

    @Override
    public HashMap<Integer, ArrayList<Announcement>> readGeneral(Packet packet) {
        HashMap<Integer, ArrayList<Announcement>> tempResponse = new HashMap<>();

        ArrayList<Announcement> tempA1 = new ArrayList<>();
        Announcement ann1 = new Announcement("ReadGeneral".toCharArray(), new User(123, client1.getPubKey()), null, 1);
        ann1.setWts(1);
        ann1.setId("G0_1".toCharArray());
        MessageSigner.sign(ann1, client1.getPrvKey());
        tempA1.add(ann1);

        ArrayList<Announcement> tempA2 = new ArrayList<>();
        Announcement ann2 = new Announcement("ReadGeneral2".toCharArray(), new User(1, client1.getPubKey()), null, 1);
        ann2.setWts(2);
        ann2.setId("G0_2".toCharArray());
        MessageSigner.sign(ann2, client1.getPrvKey());
        tempA2.add(ann2);
        Announcement ann3 = new Announcement("ReadGeneral3".toCharArray(), new User(3, client1.getPubKey()), null, 1);
        ann3.setWts(2);
        ann3.setId("G0_3".toCharArray());
        MessageSigner.sign(ann3, client1.getPrvKey());
        tempA2.add(ann3);
        Announcement ann4 = new Announcement("ReadGeneral4".toCharArray(), new User(2, client1.getPubKey()), null, 1);
        ann4.setWts(2);
        ann4.setId("G0_4".toCharArray());
        MessageSigner.sign(ann4, client1.getPrvKey());
        tempA2.add(ann4);

        ArrayList<Announcement> tempA3 = new ArrayList<>();
        Announcement ann5 = new Announcement("ReadGeneral5".toCharArray(), new User(123, client1.getPubKey()), null, 1);
        ann5.setWts(3);
        ann5.setId("G0_5".toCharArray());
        MessageSigner.sign(ann5, client1.getPrvKey());
        tempA3.add(ann5);

        tempResponse.put(1, tempA1);
        tempResponse.put(2, tempA2);
        tempResponse.put(3, tempA3);

        return tempResponse;
    }

    @Override
    public Announcement getAnnouncementById(char[] id, Packet packet) throws AnnouncementNotFoundException {

        if (Arrays.equals(id, "222".toCharArray()))
            throw new AnnouncementNotFoundException();
        else if (Arrays.equals(id, "111".toCharArray()) && server.getId() > 3)
            throw new AnnouncementNotFoundException();


        Announcement ann = new Announcement("TEST_ANN".toCharArray(), new User(123, client1.getPubKey()), null, 1);
        ann.setWts(1);
        if (Arrays.equals(id, "333".toCharArray()))
            ann.setId("Err".toCharArray());
        else
            ann.setId(id);
        MessageSigner.sign(ann, client1.getPrvKey());

        return ann;
    }

    @Override
    public User getUserById(int id, Packet packet) throws UserNotFoundException {
        packet.setWts(2);
        if (id == 123456)
            return new User(123456, client1.getPubKey());
        if (id == 123 && server.getId() > 3) {
            throw new UserNotFoundException();
        }
        if (id == 123)
            return new User(123, client2.getPubKey());

        if (id == 111) {
            throw new UserNotFoundException();
        }

        if (id == 444) {
            return new User(1231, client2.getPubKey());
        }

        return null;
    }

    @Override
    public int getRegisterWts() {
        return 0;
    }

    @Override
    public synchronized int getChannelWts(int board, PublicKey ownerPk) throws UserNotFoundException {
        if (board == 0 && ownerPk == null)
            throw new UserNotFoundException();

        return 2;
    }

    @Override
    public void readWb(PublicKey pk, Map<Integer, ArrayList<Announcement>> announcements) {

    }

    @Override
    public void readGeneralWb(PublicKey pk, Map<Integer, ArrayList<Announcement>> announcements)  {

    }

    @Override
    public void announcementByIdWb(PublicKey pk, Announcement ann) throws UserNotFoundException {

        if (pk == null)
            throw new UserNotFoundException();
    }

    @Override
    public void userByIdWb(PublicKey pk, User user) {
    }
}
