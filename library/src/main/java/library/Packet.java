package library;

import commonClasses.Announcement;
import commonClasses.User;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.Random;

public class Packet implements Serializable {
    private static final Random random = new Random();

    public Packet() {
        this.nonce = random.nextInt();
    }

    public Integer getNonce() {
        return nonce;
    }

    public void setNonce(Integer nonce) {
        this.nonce = nonce;
    }

    public PublicKey getReceiverPk() {
        return receiverPk;
    }

    public void setReceiverPk(PublicKey receiverPk) {
        this.receiverPk = receiverPk;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public int getWts() {
        return wts;
    }

    public void setWts(int wts) {
        this.wts = wts;
    }

    public enum Func {
        GET_WTS,
        REGISTER,
        POST,
        POST_GENERAL,
        READ,
        READ_GENERAL,
        GET_ANN_ID,
        GET_USER_ID,
        ERROR,
        USER_NOT_FOUND,
        INVALID_ANN,
        ANN_NOT_FOUND
    }

    private Func function;
    private Announcement[] announcements;
    private User user;
    private PublicKey senderPk;
    private PublicKey receiverPk;
    private int numberOfAnnouncements;
    private char[] message;
    private int id;
    private String username;
    private byte[] messageSignature;

    private Integer nonce;
    private long timestamp;
    private int seqNumber;
    private byte[] sign = null;

    //REGS
    private int rid;
    private int wts;


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

    public PublicKey getSenderPk() {
        return senderPk;
    }

    public void setSenderPk(PublicKey key) {
        this.senderPk = key;
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

    public int getSeqNumber() { return seqNumber; }

    public void setSeqNumber(int seqNumber) { this.seqNumber = seqNumber; }

}
