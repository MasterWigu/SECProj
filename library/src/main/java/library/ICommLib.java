package library;

import java.rmi.*;
import java.security.PublicKey;
import commonClasses.*;
import commonClasses.exceptions.AnnouncementNotFoundException;
import commonClasses.exceptions.UserNotFoundException;

/** Tic Tac Toe game remote interface. */
public interface ICommLib extends Remote {
    String register(PublicKey key, String username) throws RemoteException;

    String post(PublicKey key, char[] message, Announcement[] a) throws RemoteException, UserNotFoundException;

    String postGeneral(PublicKey key, char[] message, Announcement[] a) throws RemoteException, UserNotFoundException;

    Announcement[] read(PublicKey key, int number) throws RemoteException, UserNotFoundException;

    Announcement[] readGeneral(int number) throws RemoteException;

    Announcement getAnnouncementById(int id) throws RemoteException, AnnouncementNotFoundException;

    User getUserById(int id) throws  RemoteException, UserNotFoundException;

}
