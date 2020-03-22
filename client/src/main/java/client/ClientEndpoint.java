package client;

import commonClasses.Announcement;
import commonClasses.User;
import commonClasses.exceptions.AnnouncementNotFoundException;
import commonClasses.exceptions.UserNotFoundException;
import library.Packet;
import library.PacketSigner;
import library.SocketClient;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

public class ClientEndpoint {
    private SocketClient socketClient;
    private PrivateKey clientPrivateKey;

    public ClientEndpoint(String h, int p, PrivateKey cpk, PublicKey spk){
        socketClient = new SocketClient(h, p, spk);
        clientPrivateKey = cpk;
    }


    public String register(PublicKey key, String username) {
        Packet request = new Packet();

        request.setFunction(Packet.Func.REGISTER);
        request.setKey(key);
        request.setUsername(username);

        Packet response = socketClient.sendFunction(request, clientPrivateKey);
        return Arrays.toString(response.getMessage());
    }


    public String post(PublicKey key, char[] message, Announcement[] a) throws UserNotFoundException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.POST);
        request.setKey(key);
        request.setMessage(message);
        request.setAnnouncements(a);

        Packet response = socketClient.sendFunction(request, clientPrivateKey);
        return response.getMessage().toString();
    }

    public String postGeneral(PublicKey key, char[] message, Announcement[] a) throws UserNotFoundException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.POST_GENERAL);
        request.setKey(key);
        request.setMessage(message);
        request.setAnnouncements(a);

        Packet response = socketClient.sendFunction(request, clientPrivateKey);
        return response.getMessage().toString();
    }


    public Announcement[] read(PublicKey key, int number) throws UserNotFoundException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.READ);
        request.setKey(key);
        request.setNumberOfAnnouncements(number);

        Packet response = socketClient.sendFunction(request, clientPrivateKey);
        return response.getAnnouncements();
    }


    public Announcement[] readGeneral(PublicKey key, int number) {
        Packet request = new Packet();

        request.setFunction(Packet.Func.READ_GENERAL);
        request.setNumberOfAnnouncements(number);
        request.setKey(key);

        Packet response = socketClient.sendFunction(request, clientPrivateKey);
        return response.getAnnouncements();
    }


    public Announcement getAnnouncementById(PublicKey key, int id) throws AnnouncementNotFoundException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.GET_ANN_ID);
        request.setId(id);
        request.setKey(key);

        Packet response = socketClient.sendFunction(request,clientPrivateKey);

        Announcement ann;
        if (response.getFunction() == Packet.Func.GET_ANN_ID) {
            ann = response.getAnnouncements()[0];
        } else {
            throw new AnnouncementNotFoundException();
        }

        if (ann == null) {
            throw new AnnouncementNotFoundException();
        }
        return null;
    }


    public User getUserById(PublicKey key, int id) throws UserNotFoundException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.GET_USER_ID);
        request.setId(id);
        request.setKey(key);

        // TODO im confused - this needs confirmation
        // where is the list of users

        return null;
    }
}
