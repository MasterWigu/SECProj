package library;

import commonClasses.Announcement;
import commonClasses.User;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.Arrays;

public class Packet implements Serializable {

    public enum Func {
        REGISTER,
        POST,
        POST_GENERAL,
        READ,
        READ_GENERAL,
        GET_ANN_ID,
        GET_USER_ID,
        ERROR,
        USER_NOT_FOUND
    }

    private Func function;
    private Announcement[] announcements;
    private User user;
    private PublicKey key;
    private int numberOfAnnouncements;
    private char[] message;
    private int id;
    private String username;
    private byte[] messageSignature;

    private long timestamp;
    private byte[] sign = null;

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

    public byte[] getSign() {
        return sign;
    }

    public void setSign(byte[] signature) {
        this.sign = signature;
    }

    public byte[] getMessageSignature() {
        return messageSignature;
    }

    public void setMessageSignature(byte[] messageSignature) {
        this.messageSignature = messageSignature;
    }

}
