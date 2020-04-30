package library;

import commonClasses.Announcement;
import commonClasses.User;
import commonClasses.exceptions.*;
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
        switch (packet.getFunction()){
            case GET_WTS:
                response = handleGetWts(packet, response);
                break;
            case WRITE_BACK:
                response = handleWriteBack(packet, response);
            case REGISTER:
                try {
                    String register = aDPASService.register(packet.getSenderPk(), packet.getWts());
                    response.setFunction(REGISTER);
                    response.setMsg(register.toCharArray());
                    response.setWts(packet.getWts());
                } catch (KeyException e) {
                    response.setFunction(ERROR);
                    response.setMsg("Invalid Public Key.".toCharArray());
                } catch (InvalidWtsException e) {
                    response.setFunction(ERROR);
                    response.setMsg("Invalid WTS on register.".toCharArray());
                }
                break;
            case POST:
                try {
                    String post = aDPASService.post(packet.getSenderPk(), packet.getSingleAnnouncement(), packet.getWts());
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
                    String postGeneral = aDPASService.postGeneral(packet.getSenderPk(), packet.getMessage(), packet.getAnnouncements(), packet.getTimestamp(), packet.getMessageSignature());
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
                    Announcement[] a = aDPASService.read(packet.getSenderPk(), packet.getNumberOfAnnouncements());
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


    private Packet handleGetWts(Packet pack, Packet response) {
        response.setFunction(GET_WTS);
        switch (pack.getAuxFunction()){
            case REGISTER:
                response.setAuxFunction(REGISTER);
                response.setWts(aDPASService.getRegisterWts());
                break;
            case POST:
                try {
                    response.setAuxFunction(POST);
                    response.setWts(aDPASService.getChannelWts(0, pack.getUser().getPk()));
                } catch (UserNotFoundException e) {
                    response.setFunction(USER_NOT_FOUND);
                }
                break;
            case POST_GENERAL:
                try{
                    response.setAuxFunction(POST_GENERAL);
                    response.setWts(aDPASService.getChannelWts(1, null));
                } catch (UserNotFoundException e){
                    response.setFunction(USER_NOT_FOUND);
                }
                break;
            default:
                response.setFunction(ERROR);
        }
        return response;
    }

    private Packet handleWriteBack(Packet pack, Packet response) {
        response.setFunction(WRITE_BACK);
        switch (pack.getAuxFunction()) {
            case READ:
                try{
                    Announcement[] a = aDPASService.read(packet.getSenderPk(), packet.getNumberOfAnnouncements());
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
    }
}
