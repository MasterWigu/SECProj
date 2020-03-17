package commonClasses;

import java.io.Serializable;
import java.rmi.*;
import java.security.PublicKey;


public class Announcement implements Serializable {

    private int id;
    private char[] message;
    private User creator;

    public Announcement(int aid, char[] mss, User crt) {
        id = aid;
        message = mss;
        creator = crt;
    }


    public char[] getMessage() {
        return message;
    }

    public User getCreator() {
        return creator;
    }

    public int getId() {
        return id;
    }
}
