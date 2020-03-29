package commonClasses;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;


public class Announcement implements Serializable {

    private int id;
    private char[] message;
    private User creator;
    private int board; // 0 if personal, 1 if general
    private long timestamp;
    private Announcement[] reffs;

    private byte[] signature;

    public Announcement(char[] mss, User crt, Announcement[] rs, int br, long time, byte[] sign) {
        message = mss;
        creator = crt;
        board = br;
        timestamp = time;
        reffs = rs;
        signature = sign;
    }


    public char[] getMessage() {
        return message;
    }

    public User getCreator() {
        return creator;
    }

    public int getBoard() {
        return board;
    }

    public int getId() {
        return id;
    }

    public void setId(int aid) {
        id = aid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Announcement[] getReffs() {
        return reffs;
    }

    public String toString() {
        String out ="";
        out += "----------- Announcement -----------\n";
        if (board == 1) {
            out += "Board: General\n";
        } else {
            out += "Board: Personal\n";
        }
        out += "Ann. Id: " + id + "\n";
        out += "Creator Id: " + creator.getId() + "\n";
        out += "Time: " + new Timestamp(timestamp).toString() + "\n";
        out += "Refers to: ";
        for (Announcement a : reffs) {
            out += a.getId()+", ";
        }
        if (reffs.length != 0) {
            out = out.substring(0, out.length()-2);
        }
        out += "\n";
        out += "Message:\n    " + String.valueOf(message) + "\n";
        out += "--------- End Announcement ---------\n";
        return out;
    }



    public boolean equals(Announcement ann) {
        if (!getCreator().equals(ann.getCreator())) {
            return false;
        }
        if (!Arrays.equals(getMessage(), ann.getMessage())) {
            return false;
        }
        if (getBoard() != ann.getBoard()) {
            return false;
        }
        if (!Arrays.deepEquals(getReffs(), ann.getReffs())) {
            return false;
        }
        return ann.getTimestamp() == getTimestamp();

    }

    public byte[] getSignature() {
        return signature;
    }
}
