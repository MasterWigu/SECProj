package library;

import commonClasses.Announcement;
import commonClasses.User;
import commonClasses.exceptions.AnnouncementNotFoundException;
import library.Exceptions.CommunicationErrorException;
import commonClasses.exceptions.InvalidAnnouncementException;
import commonClasses.exceptions.UserNotFoundException;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class ClientEndpoint {
    private SocketClient socketClient;
    private PrivateKey clientPrivateKey;

    // TODO communication
    public ClientEndpoint(String h, int[] p, PrivateKey cpk, List<PublicKey> spk){
        socketClient = new SocketClient(h, p[0], spk.get(0));
        clientPrivateKey = cpk;

    }


    public String register(PublicKey key, String username) throws CommunicationErrorException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.REGISTER);
        request.setKey(key);
        request.setUsername(username);

        Packet response = socketClient.sendFunction(request, clientPrivateKey);
        if (response.getFunction() == Packet.Func.REGISTER)
            return String.valueOf(response.getMessage());
        else
            throw new CommunicationErrorException();
    }


    public char[] post(PublicKey key, char[] message, Announcement[] a, byte[] msgSign) throws UserNotFoundException, InvalidAnnouncementException, CommunicationErrorException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.POST);
        request.setKey(key);
        request.setMessage(message);
        request.setAnnouncements(a);
        request.setMessageSignature(msgSign);

        Packet response = socketClient.sendFunction(request, clientPrivateKey);
        if (response.getFunction() == Packet.Func.USER_NOT_FOUND)
            throw new UserNotFoundException();
        else if (response.getFunction() == Packet.Func.INVALID_ANN)
            throw  new InvalidAnnouncementException();
        else if (response.getFunction() == Packet.Func.POST)
            return response.getMessage();
        else
            throw new CommunicationErrorException();
    }

    public char[] postGeneral(PublicKey key, char[] message, Announcement[] a, byte[] msgSign) throws UserNotFoundException, InvalidAnnouncementException, CommunicationErrorException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.POST_GENERAL);
        request.setKey(key);
        request.setMessage(message);
        request.setAnnouncements(a);
        request.setMessageSignature(msgSign);

        Packet response = socketClient.sendFunction(request, clientPrivateKey);
        if (response.getFunction() == Packet.Func.USER_NOT_FOUND)
            throw new UserNotFoundException();
        else if (response.getFunction() == Packet.Func.INVALID_ANN)
            throw  new InvalidAnnouncementException();
        else if (response.getFunction() == Packet.Func.POST_GENERAL)
            return response.getMessage();
        else
            throw new CommunicationErrorException();
    }


    public Announcement[] read(PublicKey key, int number) throws UserNotFoundException, CommunicationErrorException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.READ);
        request.setKey(key);
        request.setNumberOfAnnouncements(number);

        Packet response = socketClient.sendFunction(request, clientPrivateKey);
        if (response.getFunction() == Packet.Func.USER_NOT_FOUND)
            throw new UserNotFoundException();
        else if (response.getFunction() == Packet.Func.READ)
            return response.getAnnouncements();
        else
            throw new CommunicationErrorException();
    }


    public Announcement[] readGeneral(PublicKey key, int number) throws CommunicationErrorException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.READ_GENERAL);
        request.setNumberOfAnnouncements(number);
        request.setKey(key);

        Packet response = socketClient.sendFunction(request, clientPrivateKey);

        if (response.getFunction() == Packet.Func.READ_GENERAL)
            return response.getAnnouncements();
        else
            throw new CommunicationErrorException();
    }


    public Announcement getAnnouncementById(PublicKey key, int id) throws AnnouncementNotFoundException, CommunicationErrorException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.GET_ANN_ID);
        request.setId(id);
        request.setKey(key);

        Packet response = socketClient.sendFunction(request,clientPrivateKey);

        Announcement ann;
        if (response.getAnnouncements() == null) {
            throw new AnnouncementNotFoundException();
        }
        if (response.getFunction() == Packet.Func.GET_ANN_ID) {
            ann = response.getAnnouncements()[0];
        } else if (response.getFunction() == Packet.Func.ANN_NOT_FOUND ){
            throw new AnnouncementNotFoundException();
        } else {
            throw new CommunicationErrorException();
        }
        return ann;
    }


    public User getUserById(PublicKey key, int id) throws UserNotFoundException, CommunicationErrorException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.GET_USER_ID);
        request.setId(id);
        request.setKey(key);

        Packet response = socketClient.sendFunction(request,clientPrivateKey);

        User user;
        if (response.getFunction() == Packet.Func.GET_USER_ID) {
            user = response.getUser();
        } else if (response.getFunction() == Packet.Func.USER_NOT_FOUND) {
            throw new UserNotFoundException();
        } else {
            throw new CommunicationErrorException();
        }

        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }
}
