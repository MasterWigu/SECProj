package library;

import commonClasses.Announcement;
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
    public int serverId;
    public PublicKey tempPublicKey = null;
    public Announcement tempAnn;
    public Packet tempPacket;
    public int tempId;
    public int tempWts;
    public int tempBoard;
    public Map<Integer, ArrayList<Announcement>> tempAnnMap;

    public String tempUsername;
    public char[] tempMessage;
    public Announcement[] tempAs;
    public long tempTime;
    public byte[] tempSign;
    public int tempNumber;



    // ------
    DPASEmulation(int id){
        serverId = id;
    }

    @Override
    public String register(PublicKey key, int wts) throws KeyException, InvalidWtsException {
        tempPublicKey = key;
        tempWts = wts;

        if (tempPublicKey == null)
            throw new KeyException();
        if (wts <= 0)
            throw new InvalidWtsException();

        return "UserAddedTest";
    }

    @Override
    public String post(PublicKey pk, Announcement announcement, int wts) throws UserNotFoundException, InvalidAnnouncementException {
        tempPublicKey = pk;
        tempWts = wts;
        tempAnn = announcement;

        if (tempPublicKey == null)
            throw new UserNotFoundException();
        if (tempAnn == null || Arrays.equals(tempAnn.getMessage(), "ERROR_POST".toCharArray()))
            throw new InvalidAnnouncementException();

        return "PostedCreatedTest";
    }

    @Override
    public String postGeneral(PublicKey key, Announcement announcement, int wts) throws UserNotFoundException, InvalidAnnouncementException {
        tempPublicKey = key;
        tempWts = wts;
        tempAnn = announcement;

        if (tempPublicKey == null)
            throw new UserNotFoundException();
        if (tempAnn == null || Arrays.equals(tempAnn.getMessage(), "ERROR_POST_GENERAL".toCharArray()))
            throw new InvalidAnnouncementException();

        return "PostedGeneralCreatedTest";
    }

    @Override
    public HashMap<Integer, ArrayList<Announcement>> read(PublicKey key, Packet packet) throws UserNotFoundException {
        tempPublicKey = key;
        tempPacket = packet;

        if (tempPublicKey == null)
            throw new UserNotFoundException();

        HashMap<Integer, ArrayList<Announcement>> tempResponse = new HashMap<>();
        ArrayList<Announcement> tempA = new ArrayList<>();
        tempA.add(new Announcement("READ".toCharArray(), null, null, 0));
        tempResponse.put(0, tempA);

        return tempResponse;
    }

    @Override
    public HashMap<Integer, ArrayList<Announcement>> readGeneral(Packet packet) {
        tempPacket = packet;

        HashMap<Integer, ArrayList<Announcement>> tempResponse = new HashMap<>();
        ArrayList<Announcement> tempA = new ArrayList<>();
        tempA.add(new Announcement(packet.getMessage(), null, null, 0));
        tempResponse.put(0, tempA);
        return tempResponse;
    }

    @Override
    public Announcement getAnnouncementById(char[] id, Packet packet) throws AnnouncementNotFoundException {
        tempPacket = packet;
        tempId = Integer.parseInt(new String(id));

        if (tempId == -2)
            throw new AnnouncementNotFoundException();

        return new Announcement("SuccessfulAnnouncement".toCharArray(), null, null, 0);
    }

    @Override
    public User getUserById(int id, Packet packet) throws UserNotFoundException {
        tempPacket = packet;
        tempId = id;

        if (tempId == -2)
            throw new UserNotFoundException();

        return new User(-3, null);
    }

    @Override
    public int getRegisterWts() { // TODO what is supposed to do with this?
        return 0;
    }

    @Override
    public int getChannelWts(int board, PublicKey ownerPk) throws UserNotFoundException {
        tempPublicKey = ownerPk;
        tempBoard = board;

        if (tempPublicKey == null)
            throw new UserNotFoundException();

        return 2;
    }

    @Override
    public void readWb(PublicKey pk, Map<Integer, ArrayList<Announcement>> announcements) throws UserNotFoundException, InvalidAnnouncementException {
        tempPublicKey = pk;
        tempAnnMap = announcements;

        if (tempPublicKey == null)
            throw new UserNotFoundException();
        if (tempAnn == null || Arrays.equals(tempAnnMap.get(0).get(0).getMessage(), "ERROR_READ_WB".toCharArray()))
            throw new InvalidAnnouncementException();
    }

    @Override
    public void readGeneralWb(PublicKey pk, Map<Integer, ArrayList<Announcement>> announcements) throws UserNotFoundException {
        tempPublicKey = pk;
        tempAnnMap = announcements;

        if (tempPublicKey == null)
            throw new UserNotFoundException();
    }

    @Override
    public void announcementByIdWb(PublicKey pk, Announcement ann) throws UserNotFoundException {
        tempPublicKey = pk;
        tempAnn = ann;

        if (tempPublicKey == null)
            throw new UserNotFoundException();
    }

    @Override
    public void userByIdWb(PublicKey pk, User user) { // TODO what I am suppose to do with this?
    }
}
