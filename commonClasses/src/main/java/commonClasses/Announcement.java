package commonClasses;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;


public class Announcement implements Serializable {

    private int id;
    private char[] message;
    private User creator;
    private int board; // 0 if personal, 1 if general
    private int wts;
    private Announcement[] refs;

    private byte[] signature;

    public Announcement(char[] mss, User crt, Announcement[] rs, int br, int wts1, byte[] sign) {
        message = mss;
        creator = crt;
        board = br;
        wts = wts1;
        refs = rs;
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

    public Announcement[] getRefs() {
        return refs;
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
        out += "WTS: " + wts + "\n";
        out += "Refers to: ";
        if (refs != null) {
            for (Announcement a : refs) {
                out += a.getId() + ", ";
            }
            if (refs.length != 0) {
                out = out.substring(0, out.length() - 2);
            }
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
        if (!Arrays.deepEquals(getRefs(), ann.getRefs())) {
            return false;
        }
        return ann.getWts() == getWts();

    }

    public byte[] getSignature() {
        return signature;
    }

    public int getWts() {
        return wts;
    }
}
