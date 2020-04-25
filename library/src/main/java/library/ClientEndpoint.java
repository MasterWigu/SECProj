package library;

import commonClasses.Announcement;
import commonClasses.SRData;
import commonClasses.User;
import commonClasses.exceptions.AnnouncementNotFoundException;
import javafx.scene.shape.ArcTo;
import library.Algorithms.BAtomicRegister;
import library.Exceptions.CommunicationErrorException;
import commonClasses.exceptions.InvalidAnnouncementException;
import commonClasses.exceptions.UserNotFoundException;
import library.Exceptions.PacketValidationException;

import javax.jws.soap.SOAPBinding;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ClientEndpoint {
    private AuthPerfectP2PLinks authPerfectP2PLinks;
    private SRData client;
    private ArrayList<SRData> servers;
    private BAtomicRegister atomicRegister;


    // TODO communication
    public ClientEndpoint(PrivateKey cpk, PublicKey key, ArrayList<SRData> s, int faults){
        authPerfectP2PLinks = new AuthPerfectP2PLinks();
        authPerfectP2PLinks.init();
        servers = s;

        client = new SRData();
        client.setPubKey(key);
        client.setPrvKey(cpk);
        atomicRegister = new BAtomicRegister(client, servers, faults);
    }

    public String register() throws CommunicationErrorException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.REGISTER);

        SimpleBroadcast.broadcast(request, client, servers);

        return "Success register.";
    }


    public char[] post(char[] message, Announcement[] refs, byte[] msgSign) throws UserNotFoundException, InvalidAnnouncementException, CommunicationErrorException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.POST);

        User user = new User(-1, client.getPubKey());
        Announcement ann = new Announcement(message, user, refs, 0, 0,  msgSign);
        request.setAnnToWrite(ann);

        Packet response = atomicRegister.write(request);

        if (response.getFunction() == Packet.Func.USER_NOT_FOUND)
            throw new UserNotFoundException();
        else if (response.getFunction() == Packet.Func.INVALID_ANN)
            throw  new InvalidAnnouncementException();
        else if (response.getFunction() == Packet.Func.POST_GENERAL)
            return response.getMsg();
        else
            throw new CommunicationErrorException();
    }


    public char[] postGeneral(char[] message, Announcement[] refs, byte[] msgSign) throws UserNotFoundException, InvalidAnnouncementException, CommunicationErrorException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.POST);

        User user = new User(-1, client.getPubKey());
        Announcement ann = new Announcement(message, user, refs, 1, 0,  msgSign);
        request.setAnnToWrite(ann);

        Packet response = atomicRegister.write(request);

        if (response.getFunction() == Packet.Func.USER_NOT_FOUND)
            throw new UserNotFoundException();
        else if (response.getFunction() == Packet.Func.INVALID_ANN)
            throw  new InvalidAnnouncementException();
        else if (response.getFunction() == Packet.Func.POST_GENERAL)
            return response.getMsg();
        else
            throw new CommunicationErrorException();

    }

    /*
    public Announcement[] read(PublicKey key, int number) throws UserNotFoundException, CommunicationErrorException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.READ);
        request.setSenderPk(key);
        request.setNumberOfAnnouncements(number);

        Packet response = authPerfectP2PLinks.sendFunction(request, clientPrivateKey);
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
        request.setSenderPk(key);

        Packet response = authPerfectP2PLinks.sendFunction(request, clientPrivateKey);

        if (response.getFunction() == Packet.Func.READ_GENERAL)
            return response.getAnnouncements();
        else
            throw new CommunicationErrorException();
    }


    public Announcement getAnnouncementById(PublicKey key, int id) throws AnnouncementNotFoundException, CommunicationErrorException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.GET_ANN_ID);
        request.setId(id);
        request.setSenderPk(key);

        Packet response = authPerfectP2PLinks.sendFunction(request,clientPrivateKey);

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
        request.setSenderPk(key);

        Packet response = authPerfectP2PLinks.sendFunction(request,clientPrivateKey);

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
     */
}
