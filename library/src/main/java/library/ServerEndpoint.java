package library;

import commonClasses.Announcement;
import commonClasses.User;
import commonClasses.exceptions.*;
import library.Interfaces.ICommLib;
import library.Interfaces.ISocketProcessor;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
                response.setRid(packet.getRid());
                break;
            case WRITE_BACK:
                //TODO do this shit
                response = handleWriteBack(packet, response);
            case REGISTER:
                response.setFunction(REGISTER);
                response.setWts(packet.getWts());
                try {
                    String register = aDPASService.register(packet.getSenderPk(), packet.getWts());
                    response.setMessage(register.toCharArray());
                } catch (KeyException e) {
                    response.setFunction(ERROR);
                    response.setMessage("Invalid Public Key.".toCharArray());
                } catch (InvalidWtsException e) {
                    response.setFunction(ERROR);
                    response.setMessage("Invalid WTS on register.".toCharArray());
                }
                break;
            case POST:
                response.setFunction(POST);
                response.setWts(packet.getWts());
                try {
                    String post = aDPASService.post(packet.getSenderPk(), packet.getSingleAnnouncement(), packet.getWts());
                    response.setMessage(post.toCharArray());
                } catch (UserNotFoundException e) {
                    response.setFunction(USER_NOT_FOUND);
                } catch (InvalidAnnouncementException e) {
                    response.setFunction(INVALID_ANN);
                }
                break;
            case POST_GENERAL:
                response.setFunction(POST_GENERAL);
                response.setWts(packet.getWts());
                try{
                    String postGeneral = aDPASService.postGeneral(packet.getSenderPk(), packet.getSingleAnnouncement(), packet.getWts());
                    response.setMessage(postGeneral.toCharArray());
                } catch (UserNotFoundException e){
                    response.setFunction(USER_NOT_FOUND);
                } catch (InvalidAnnouncementException e) {
                    response.setFunction(INVALID_ANN);
                }
                break;
            case READ:
                response.setFunction(READ);
                response.setRid(packet.getRid());
                try{
                    HashMap<Integer, ArrayList<Announcement>> a = aDPASService.read(packet.getSenderPk(), response);
                    response.setAnnouncements(a);
                } catch (UserNotFoundException e){
                    response.setFunction(USER_NOT_FOUND);
                }
                break;
            case READ_GENERAL:
                response.setFunction(READ_GENERAL);
                response.setRid(packet.getRid());

                Map<Integer, ArrayList<Announcement>> a = aDPASService.readGeneral(response);
                response.setAnnouncements(a);

                break;
            case GET_ANN_ID:
                response.setFunction(GET_ANN_ID);
                response.setRid(packet.getRid());
                try {
                    Announcement ann = aDPASService.getAnnouncementById(packet.getCharId(), response);
                    response.setSingleAnnouncement(ann);
                } catch (AnnouncementNotFoundException e){
                    response.setFunction(ANN_NOT_FOUND);
                }
                break;
            case GET_USER_ID:
                response.setFunction(GET_USER_ID);
                response.setRid(response.getRid());
                try {
                    User user = aDPASService.getUserById(packet.getId(), response);
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
        /*switch (pack.getAuxFunction()) {
            case READ:
                try{
                    Map<Integer, ArrayList<Announcement>> a = aDPASService.read(packet.getSenderPk(), packet.getNumberOfAnnouncements());
                    response.setFunction(READ);
                    response.setAnnouncements(a);
                } catch (UserNotFoundException e){
                    response.setFunction(USER_NOT_FOUND);
                }
                break;
            case READ_GENERAL:
                Map<Integer, ArrayList<Announcement>> a = aDPASService.readGeneral(packet.getNumberOfAnnouncements());
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
        }*/
        return  null;
    }
}
