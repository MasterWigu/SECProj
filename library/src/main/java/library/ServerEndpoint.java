package library;

import commonClasses.Announcement;
import commonClasses.User;
import commonClasses.exceptions.AnnouncementNotFoundException;
import commonClasses.exceptions.InvalidAnnouncementException;
import commonClasses.exceptions.KeyException;
import commonClasses.exceptions.UserNotFoundException;
import library.Interfaces.ICommLib;
import library.Interfaces.ISocketProcessor;
import java.security.PublicKey;

import static library.Packet.Func.*;

public class ServerEndpoint implements ISocketProcessor {

    private PublicKey serverPublicKey;
    private ICommLib aDPASService;

    public ServerEndpoint(ICommLib aDPASS, PublicKey pk) {
        aDPASService = aDPASS;
        serverPublicKey = pk;
    }


    @Override
    public Packet doOperation(Packet packet) {
        Packet response = new Packet();
        response.setKey(serverPublicKey);
        switch (packet.getFunction()){
            case REGISTER:
                try {
                    String register = aDPASService.register(packet.getKey(), packet.getUsername());
                    response.setFunction(REGISTER);
                    response.setMessage(register.toCharArray());
                } catch (KeyException e) {
                    response.setFunction(ERROR);
                    response.setMessage("Invalid Public Key.".toCharArray());
                }
                break;
            case POST:
                try {
                    String post = aDPASService.post(packet.getKey(), packet.getMessage(), packet.getAnnouncements(), packet.getTimestamp(), packet.getMessageSignature());
                    response.setFunction(POST);
                    response.setMessage(post.toCharArray());
                } catch (UserNotFoundException e) {
                    response.setFunction(USER_NOT_FOUND);
                } catch (InvalidAnnouncementException e) {
                    response.setFunction(INVALID_ANN);
                }
                break;
            case POST_GENERAL:
                try{
                    String postGeneral = aDPASService.postGeneral(packet.getKey(), packet.getMessage(), packet.getAnnouncements(), packet.getTimestamp(), packet.getMessageSignature());
                    response.setFunction(POST_GENERAL);
                    response.setMessage(postGeneral.toCharArray());
                } catch (UserNotFoundException e){
                    response.setFunction(USER_NOT_FOUND);
                } catch (InvalidAnnouncementException e) {
                    response.setFunction(INVALID_ANN);
                }
                break;
            case READ:
                try{
                    Announcement[] a = aDPASService.read(packet.getKey(), packet.getNumberOfAnnouncements());
                    response.setFunction(READ);
                    response.setAnnouncements(a);
                } catch (UserNotFoundException e){
                    response.setFunction(USER_NOT_FOUND);
                }
                break;
            case READ_GENERAL:
                Announcement[] a = aDPASService.readGeneral(packet.getNumberOfAnnouncements());
                response.setFunction(READ_GENERAL);
                response.setAnnouncements(a);
                break;
            case GET_ANN_ID:
                try {
                    Announcement ann = aDPASService.getAnnouncementById(packet.getId());
                    response.setFunction(GET_ANN_ID);
                    response.setAnnouncements(new Announcement[]{ann});
                } catch (AnnouncementNotFoundException e){
                    response.setFunction(ANN_NOT_FOUND);
                }
                break;
            case GET_USER_ID:
                try {
                    User user = aDPASService.getUserById(packet.getId());
                    response.setFunction(GET_USER_ID);
                    response.setUser(user);
                } catch (UserNotFoundException e){
                    response.setFunction(USER_NOT_FOUND);
                }
                break;
            default:
                response.setFunction(ERROR);
        }
        return response;
    }
}
