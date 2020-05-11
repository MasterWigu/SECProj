package library;

import commonClasses.Announcement;
import commonClasses.MessageSigner;
import commonClasses.SRData;
import commonClasses.User;
import commonClasses.exceptions.*;
import library.Interfaces.ICommLib;

import javax.swing.*;
import java.lang.reflect.Array;
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
    public String register(PublicKey key, int wts) throws KeyException, InvalidWtsException {

        if (key == null)
            throw new KeyException();
        if (wts <= 0)
            throw new InvalidWtsException();

        return "UserAddedTest";
    }

    @Override
    public String post(PublicKey pk, Announcement announcement, int wts) throws UserNotFoundException, InvalidAnnouncementException {

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

        Announcement ann = new Announcement("ReadGeneral".toCharArray(), new User(123, client1.getPubKey()), null, 1);
        ann.setWts(1);
        ann.setId("G0_1".toCharArray());
        MessageSigner.sign(ann, client1.getPrvKey());

        HashMap<Integer, ArrayList<Announcement>> tempResponse = new HashMap<>();
        ArrayList<Announcement> tempA = new ArrayList<>();
        tempA.add(ann);
        tempResponse.put(1, tempA);
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
    public int getChannelWts(int board, PublicKey ownerPk) throws UserNotFoundException {
        if (board == 0 && ownerPk == null)
            throw new UserNotFoundException();

        return 2;
    }

    @Override
    public void readWb(PublicKey pk, Map<Integer, ArrayList<Announcement>> announcements) throws UserNotFoundException, InvalidAnnouncementException {

    }

    @Override
    public void readGeneralWb(PublicKey pk, Map<Integer, ArrayList<Announcement>> announcements) throws UserNotFoundException {

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
