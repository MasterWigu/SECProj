package library.Interfaces;

import commonClasses.Announcement;
import commonClasses.User;
import commonClasses.exceptions.*;
import library.Packet;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public interface ICommLib {
    String register(PublicKey key, int wts, Packet p) throws KeyException, InvalidWtsException;

    String post(PublicKey pk, Announcement announcement, int wts) throws UserNotFoundException, InvalidAnnouncementException;

    String postGeneral(PublicKey key, Announcement announcement, int wts) throws UserNotFoundException, InvalidAnnouncementException;

    HashMap<Integer, ArrayList<Announcement>> read(PublicKey key, Packet packet) throws UserNotFoundException;

    HashMap<Integer, ArrayList<Announcement>> readGeneral(Packet packet);

    Announcement getAnnouncementById(char[] id, Packet packet) throws AnnouncementNotFoundException;

    User getUserById(int id, Packet packet) throws UserNotFoundException;

    int getRegisterWts();

    int getChannelWts(int board, PublicKey ownerPk) throws UserNotFoundException;

    void readWb(PublicKey pk, Map<Integer, ArrayList<Announcement>> announcements) throws UserNotFoundException, InvalidAnnouncementException;

    void readGeneralWb(PublicKey pk, Map<Integer, ArrayList<Announcement>> announcements) throws UserNotFoundException;

    void announcementByIdWb(PublicKey pk, Announcement ann) throws UserNotFoundException;

    void userByIdWb(PublicKey pk, User user);
}
