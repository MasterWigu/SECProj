package commonClasses;

import java.io.Serializable;
import java.util.Arrays;


public class Announcement implements Serializable {

    private int id;
    private char[] message;
    private User creator;
    private int board; // 0 if personal, 1 if general

    public Announcement(int aid, char[] mss, User crt, int br) {
        id = aid;
        message = mss;
        creator = crt;
        board = br;
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

    public String toString() {
        return "Announcement:\n    Id: " + id +  "\n    Creator: " + creator + "\n    Board: " + board + "\n    Message: " + Arrays.toString(message);
    }
}
