package library.Interfaces;

import java.security.PublicKey;
import commonClasses.*;
import commonClasses.exceptions.AnnouncementNotFoundException;
import commonClasses.exceptions.InvalidAnnouncementException;
import commonClasses.exceptions.KeyException;
import commonClasses.exceptions.UserNotFoundException;


public interface ICommLib {
    String register(PublicKey key, String username) throws KeyException;

    String post(PublicKey key, char[] message, Announcement[] a, long time, byte[] sign) throws UserNotFoundException, InvalidAnnouncementException;

    String postGeneral(PublicKey key, char[] message, Announcement[] a, long time, byte[] sign) throws UserNotFoundException, InvalidAnnouncementException;

    Announcement[] read(PublicKey key, int number) throws UserNotFoundException;

    Announcement[] readGeneral(int number);

    Announcement getAnnouncementById(int id) throws AnnouncementNotFoundException;

    User getUserById(int id) throws UserNotFoundException;

}
