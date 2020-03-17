package library;

import java.rmi.*;
import java.security.PublicKey;
import commonClasses.*;

/** Tic Tac Toe game remote interface. */
public interface ICommLib extends Remote {
    String register(User user) throws RemoteException;

    String post(PublicKey key, char[] message, Announcement[] a) throws RemoteException;

    String postGeneral(PublicKey key, char[] message, Announcement[] a) throws RemoteException;

    Announcement[] read(PublicKey key, int number) throws RemoteException;

    Announcement[] readGeneral(int number) throws RemoteException;

}
