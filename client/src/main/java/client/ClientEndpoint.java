package client;

import commonClasses.Announcement;
import commonClasses.User;
import commonClasses.exceptions.AnnouncementNotFoundException;
import commonClasses.exceptions.UserNotFoundException;
import library.Packet;
import library.SocketClient;

import java.security.PublicKey;

public class ClientEndpoint {
    private SocketClient socketClient;

    public ClientEndpoint(String h, int p){
        socketClient = new SocketClient(h, p);
    }


    public String register(PublicKey key, String username) {
        Packet request = new Packet();

        request.setFunction(Packet.Func.REGISTER);
        request.setKey(key);
        request.setUsername(username);

        Packet response = socketClient.sendFunction(request);
        return null;
    }


    public String post(PublicKey key, char[] message, Announcement[] a) throws UserNotFoundException {
        return null;
    }

    public String postGeneral(PublicKey key, char[] message, Announcement[] a) throws UserNotFoundException {
        return null;
    }


    public Announcement[] read(PublicKey key, int number) throws UserNotFoundException {
        return new Announcement[0];
    }


    public Announcement[] readGeneral(int number) {
        return new Announcement[0];
    }


    public Announcement getAnnouncementById(int id) throws AnnouncementNotFoundException {
        return null;
    }


    public User getUserById(int id) throws UserNotFoundException {
        return null;
    }
}
