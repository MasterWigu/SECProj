package library.Interfaces;

import java.rmi.*;
import java.security.PublicKey;
import commonClasses.*;
import commonClasses.exceptions.AnnouncementNotFoundException;
import commonClasses.exceptions.UserNotFoundException;


public interface ICommLib extends Remote {
    String register(PublicKey key, String username);

    String post(PublicKey key, char[] message, Announcement[] a) throws RemoteException, UserNotFoundException;

    String postGeneral(PublicKey key, char[] message, Announcement[] a) throws UserNotFoundException;

    Announcement[] read(PublicKey key, int number) throws UserNotFoundException;

    Announcement[] readGeneral(int number);

    Announcement getAnnouncementById(int id) throws AnnouncementNotFoundException;

    User getUserById(int id) throws UserNotFoundException;

}
