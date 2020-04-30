package library.Interfaces;

import java.security.PublicKey;
import commonClasses.*;
import commonClasses.exceptions.*;


public interface ICommLib {
    String register(PublicKey key, int wts) throws KeyException, InvalidWtsException;

    String post(PublicKey pk, Announcement announcement, int wts) throws UserNotFoundException, InvalidAnnouncementException;

    String postGeneral(PublicKey key, char[] message, Announcement[] a, long time, byte[] sign) throws UserNotFoundException, InvalidAnnouncementException;

    Announcement[] read(PublicKey key, int number) throws UserNotFoundException;

    Announcement[] readGeneral(int number);

    Announcement getAnnouncementById(int id) throws AnnouncementNotFoundException;

    User getUserById(int id) throws UserNotFoundException;

    int getRegisterWts();

    int getChannelWts(int board, PublicKey ownerPk) throws UserNotFoundException;

}
