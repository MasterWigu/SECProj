package server;

import commonClasses.Announcement;
import commonClasses.User;
import commonClasses.exceptions.AnnouncementNotFoundException;
import commonClasses.exceptions.UserNotFoundException;
import library.Interfaces.ICommLib;
import library.Interfaces.ISocketProcessor;
import library.Packet;

import static library.Packet.Func.*;

public class ServerEndpoint implements ISocketProcessor {

    private ICommLib aDPASService;

    public ServerEndpoint(ICommLib aDPASS) {
        aDPASService = aDPASS;
    }


    @Override
    public Packet doOperation(Packet packet) {
        Packet response;
        switch (packet.getFunction()){
            case REGISTER:
                String register = aDPASService.register(packet.getKey(), packet.getUsername());
                response = new Packet();
                response.setMessage(register.toCharArray());
                break;
            case POST:
                try {
                    String post = aDPASService.post(packet.getKey(), packet.getMessage(), packet.getAnnouncements());
                    response = new Packet();
                    response.setMessage(post.toCharArray());
                } catch (UserNotFoundException e) {
                    response = new Packet();
                    response.setFunction(USER_NOT_FOUND);
                }
                break;
            case POST_GENERAL:
                try{
                    String postGeneral = aDPASService.postGeneral(packet.getKey(), packet.getMessage(), packet.getAnnouncements());
                    response = new Packet();
                    response.setMessage(postGeneral.toCharArray());
                } catch (UserNotFoundException e){
                    response = new Packet();
                    response.setFunction(USER_NOT_FOUND);
                }
                break;
            case READ:
                try{
                    Announcement[] a = aDPASService.read(packet.getKey(), packet.getNumberOfAnnouncements());
                    response = new Packet();
                    response.setAnnouncements(a);
                } catch (UserNotFoundException e){
                    response = new Packet();
                    response.setFunction(USER_NOT_FOUND);
                }
                break;
            case READ_GENERAL:
                Announcement[] a = aDPASService.readGeneral(packet.getNumberOfAnnouncements());
                response = new Packet();
                response.setAnnouncements(a);
                break;
            case GET_ANN_ID:
                try {
                    Announcement ann = aDPASService.getAnnouncementById(packet.getId());
                    response = new Packet();
                    response.setMessage(ann.getMessage());
                    response.setUser(ann.getCreator());
                    response.setId(ann.getId());
                } catch (AnnouncementNotFoundException e){
                    response = new Packet();
                    response.setFunction(USER_NOT_FOUND);
                }
                break;
            case GET_USER_ID:
                try {
                    User user = aDPASService.getUserById(packet.getId());
                    response = new Packet();
                    response.setUsername(user.getUsername());
                    response.setKey(user.getPk());
                    response.setId(user.getId());
                } catch (UserNotFoundException e){
                    response = new Packet();
                    response.setFunction(USER_NOT_FOUND);
                }
                break;
            default:
                response = new Packet();
                response.setFunction(ERROR);
        }
        return response;
    }
}
