package commonClasses;

import java.io.Serializable;
import java.util.Arrays;


public class Announcement implements Serializable {

    private char[] id;
    private char[] message;
    private User creator;
    private int board; // 0 if personal, 1 if general
    private int wts;
    private Announcement[] refs;

    private byte[] signature;

    /*public Announcement(char[] mss, User crt, Announcement[] rs, int br, int wts1, byte[] sign) {
        message = mss;
        creator = crt;
        board = br;
        wts = wts1;
        refs = rs;
        signature = sign;
    }*/

    public Announcement(char[] mss, User crt, Announcement[] rs, int br) {
        message = mss;
        creator = crt;
        board = br;
        wts = -1;
        refs = rs;
        signature = null;
    }



    public char[] getMessage() {
        return message;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public int getBoard() {
        return board;
    }

    public char[] getId() {
        return id;
    }

    public void setId(char[] aid) {
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
        out += "Ann. Id: " + String.valueOf(id) + "\n";
        out += "Creator Id: " + creator.getId() + "\n";
        out += "WTS: " + wts + "\n";
        out += "Refers to: ";
        if (refs != null) {
            for (Announcement a : refs) {
                out += String.valueOf(a.getId()) + ", ";
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

    @Override
    public boolean equals(Object o) {
        Announcement ann;
        if(o instanceof Announcement){
            ann = (Announcement) o;
        } else {
            return false;
        }

        if (id != null && ann.getId() != null && !Arrays.equals(id, ann.getId())) {
            return false;
        }
        if (!getCreator().getPk().equals(ann.getCreator().getPk())) {
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

    public void setSignature(byte[] sign) {
        this.signature = sign;
    }

    public int getWts() {
        return wts;
    }

    public void setWts(int wts) {
        this.wts = wts;
    }
}
