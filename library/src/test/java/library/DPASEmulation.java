package library;

import commonClasses.Announcement;
import commonClasses.User;
import commonClasses.exceptions.AnnouncementNotFoundException;
import commonClasses.exceptions.UserNotFoundException;
import library.Interfaces.ICommLib;

import java.security.PublicKey;

public class DPASEmulation implements ICommLib {
    public PublicKey tempPublicKey = null;
    public String tempUsername;
    public char[] tempMessage;
    public Announcement[] tempAs;
    public long tempTime;
    public byte[] tempSign;
    public int tempNumber;
    public int tempId;

    @Override
    public String register(PublicKey key, String username) {
        tempPublicKey = key;
        tempUsername = username;
        return null;
    }

    @Override
    public String post(PublicKey key, char[] message, Announcement[] a, long time, byte[] sign) throws UserNotFoundException {
        tempPublicKey = key;
        tempMessage = message;
        tempAs = a;
        tempTime = time;
        tempSign = sign;
        return null;
    }

    @Override
    public String postGeneral(PublicKey key, char[] message, Announcement[] a, long time, byte[] sign) throws UserNotFoundException {
        tempPublicKey = key;
        tempMessage = message;
        tempAs = a;
        tempTime = time;
        tempSign = sign;
        return null;
    }

    @Override
    public Announcement[] read(PublicKey key, int number) throws UserNotFoundException {
        tempPublicKey = key;
        tempNumber = number;
        return null;
    }

    @Override
    public Announcement[] readGeneral(int number) {
        tempNumber = number;
        return null;
    }

    @Override
    public Announcement getAnnouncementById(int id) throws AnnouncementNotFoundException {
        tempId = id;
        return null;
    }

    @Override
    public User getUserById(int id) throws UserNotFoundException {
        tempId = id;
        return null;
    }
}
