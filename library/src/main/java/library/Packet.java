package library;

import commonClasses.Announcement;
import commonClasses.User;

import java.io.Serializable;
import java.security.PublicKey;

public class Packet implements Serializable {


    public enum Func {
        REGISTER,
        POST,
        POST_GENERAL,
        READ,
        READ_GENERAL,
        GET_ANN_ID,
        GET_USER_ID,
        ERROR
    }

    private Func function;
    private Announcement[] announcements;
    private User user;
    private PublicKey key;
    private int numberOfAnnouncements;
    private char[] message;
    private int id;
    private String username;

    private long timestamp;
    private String sign;

    public Func getFunction() {
        return function;
    }

    public void setFunction(Func function) {
        this.function = function;
    }

    public Announcement[] getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(Announcement[] announcements) {
        this.announcements = announcements;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PublicKey getKey() {
        return key;
    }

    public void setKey(PublicKey key) {
        this.key = key;
    }

    public int getNumberOfAnnouncements() {
        return numberOfAnnouncements;
    }

    public void setNumberOfAnnouncements(int numberOfAnnouncements) {
        this.numberOfAnnouncements = numberOfAnnouncements;
    }

    public char[] getMessage() {
        return message;
    }

    public void setMessage(char[] message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign() {
        this.sign = "AAAA"; //TODO create signature
    }

}
