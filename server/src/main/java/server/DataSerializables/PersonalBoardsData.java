package server.DataSerializables;

import commonClasses.Announcement;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

public class PersonalBoardsData implements Serializable {
    private HashMap<PublicKey, ArrayList<Announcement>> personalBoards;
    private HashMap<PublicKey, ArrayList<Announcement>> personalBoardBuffers;
    private HashMap<PublicKey, Integer> personalWtss;

    public PersonalBoardsData(HashMap<PublicKey, ArrayList<Announcement>> personalBoards,
                              HashMap<PublicKey, ArrayList<Announcement>> personalBoardBuffers,
                              HashMap<PublicKey, Integer> personalWtss) {

        this.personalBoards = SerializationUtils.clone(personalBoards);
        this.personalBoardBuffers = SerializationUtils.clone(personalBoardBuffers);
        this.personalWtss = SerializationUtils.clone(personalWtss);
    }

    public PersonalBoardsData() {
        this.personalBoards = new HashMap<>();
        this.personalBoardBuffers = new HashMap<>();
        this.personalWtss = new HashMap<>();
    }

    public HashMap<PublicKey, ArrayList<Announcement>> getPersonalBoards() {
        return personalBoards;
    }

    public HashMap<PublicKey, ArrayList<Announcement>> getPersonalBoardBuffers() {
        return personalBoardBuffers;
    }

    public HashMap<PublicKey, Integer> getPersonalWtss() {
        return personalWtss;
    }
}
