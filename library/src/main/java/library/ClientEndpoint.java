package library;

import commonClasses.Announcement;
import commonClasses.SRData;
import commonClasses.User;
import commonClasses.exceptions.AnnouncementNotFoundException;
import commonClasses.exceptions.UserNotFoundException;
import library.Algorithms.BAtomicRegister;
import library.Exceptions.CommunicationErrorException;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;


public class ClientEndpoint {
    private SRData client;
    private BAtomicRegister atomicRegister;
    private User clientUser;


    public ClientEndpoint(PrivateKey cpk, PublicKey key, ArrayList<SRData> s, int faults){

        client = new SRData();
        client.setPubKey(key);
        client.setPrvKey(cpk);
        atomicRegister = new BAtomicRegister(client, s, faults);
    }

    void setUserTests() { //just for tests
        if (clientUser == null)
            clientUser = new User(-1, client.getPubKey());
    }

    public void shutdown() {
        atomicRegister.shutdown();
    }

    public String register() {
        Packet request = new Packet();

        request.setFunction(Packet.Func.REGISTER);

        Packet response = atomicRegister.write(request);

        clientUser = response.getUser();
        return new String(response.getMessage());
    }


    public char[] post(char[] message, Announcement[] refs) throws CommunicationErrorException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.POST);

        Announcement ann = new Announcement(message, clientUser, refs, 0);
        request.setSingleAnnouncement(ann);

        Packet response = atomicRegister.write(request);


        if (response != null && response.getFunction() == Packet.Func.POST)
            return response.getMessage();
        else
            throw new CommunicationErrorException();
    }


    public char[] postGeneral(char[] message, Announcement[] refs) throws CommunicationErrorException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.POST_GENERAL);

        Announcement ann = new Announcement(message, clientUser, refs, 1);
        request.setSingleAnnouncement(ann);

        Packet response = atomicRegister.write(request);

        if (response != null && response.getFunction() == Packet.Func.POST_GENERAL)
            return response.getMessage();
        else
            throw new CommunicationErrorException();

    }


    public Announcement[] read(User user, int number) throws CommunicationErrorException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.READ);
        request.setUser(user);

        Packet response = atomicRegister.read(request);
        if (response != null && response.getFunction() == Packet.Func.READ)
            return selectAnnouncements(response.getAnnouncements(), number);
        else
            throw new CommunicationErrorException();
    }


    public Announcement[] readGeneral(int number) throws CommunicationErrorException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.READ_GENERAL);

        Packet response = atomicRegister.read(request);

        if (response.getFunction() == Packet.Func.READ_GENERAL)
            return selectAnnouncements(response.getAnnouncements(), number);
        else
            throw new CommunicationErrorException();
    }


    public Announcement getAnnouncementById(char[] id) throws AnnouncementNotFoundException, CommunicationErrorException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.GET_ANN_ID);
        request.setCharId(id);

        Packet response = atomicRegister.read(request);


        Announcement ann;
        if (response == null)
            throw new CommunicationErrorException();
        else if (response.getSingleAnnouncement() == null) {
            throw new AnnouncementNotFoundException();
        }
        if (response.getFunction() == Packet.Func.GET_ANN_ID) {
            ann = response.getSingleAnnouncement();
        } else if (response.getFunction() == Packet.Func.ANN_NOT_FOUND ){
            throw new AnnouncementNotFoundException();
        } else {
            throw new CommunicationErrorException();
        }
        return ann;
    }


    public User getUserById(int id) throws UserNotFoundException, CommunicationErrorException {
        Packet request = new Packet();

        request.setFunction(Packet.Func.GET_USER_ID);
        request.setId(id);

        Packet response = atomicRegister.read(request);

        if (response == null)
            throw new CommunicationErrorException();
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

    private Announcement[] selectAnnouncements(Map<Integer, ArrayList<Announcement>> anns, int number) {
        int annCount = 0;
        ArrayList<Announcement> outAnns = new ArrayList<>();

        ArrayList<Integer> keys = new ArrayList<>(anns.keySet());
        Collections.sort(keys, Collections.reverseOrder());

        for (Integer key : keys) {
            List<Announcement> AnnList = anns.get(key);
            Collections.sort(AnnList, new Comparator<Announcement>() {
                @Override
                public int compare(Announcement a1, Announcement a2) {
                    return Integer.compare(a1.getCreator().getId(), a2.getCreator().getId());
                }
            });

            for (Announcement ann : AnnList) {
                outAnns.add(0, ann);
                annCount++;
                if (annCount == number) {
                    return outAnns.toArray(new Announcement[0]);
                }
            }
        }
        return outAnns.toArray(new Announcement[0]);
    }


}
