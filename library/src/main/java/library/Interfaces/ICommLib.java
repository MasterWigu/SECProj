package library.Interfaces;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

import commonClasses.*;
import commonClasses.exceptions.*;
import library.Packet;


public interface ICommLib {
    String register(PublicKey key, int wts) throws KeyException, InvalidWtsException;

    String post(PublicKey pk, Announcement announcement, int wts) throws UserNotFoundException, InvalidAnnouncementException;

    String postGeneral(PublicKey key, Announcement announcement, int wts) throws UserNotFoundException, InvalidAnnouncementException;

    HashMap<Integer, ArrayList<Announcement>> read(PublicKey key, Packet packet) throws UserNotFoundException;

    HashMap<Integer, ArrayList<Announcement>> readGeneral(Packet packet);

    Announcement getAnnouncementById(char[] id, Packet packet) throws AnnouncementNotFoundException;

    User getUserById(int id, Packet packet) throws UserNotFoundException;

    int getRegisterWts();

    int getChannelWts(int board, PublicKey ownerPk) throws UserNotFoundException;

}
