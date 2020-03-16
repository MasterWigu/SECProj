package DPAS;

import java.rmi.*;
import java.security.PublicKey;

/** Tic Tac Toe game remote interface. */
public interface TTTService extends Remote {
    String register(PublicKey key, String name) throws RemoteException;

    String post(PublicKey key, String message, Annoucement player) throws RemoteException;

    int checkWinner() throws RemoteException;

    boolean jogaCantoAleatorio() throws RemoteException;

}
