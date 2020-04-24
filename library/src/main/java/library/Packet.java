package library;

import commonClasses.Announcement;
import commonClasses.User;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;
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

    public Announcement getAnnToWrite() {
        return annToWrite;
    }

    public void setAnnToWrite(Announcement annToWrite) {
        this.annToWrite = annToWrite;
    }

    public Map<Integer, List<Announcement>> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(Map<Integer, List<Announcement>> announcements) {
        this.announcements = announcements;
    }

    public enum Func {
        WRITE_BACK,
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
    private Map<Integer, List<Announcement>> announcements;
    private User user;

    private int id;
    private Announcement annToWrite;


    private Integer nonce;
    private byte[] sign = null;

    //APP2PL
    private PublicKey senderPk;
    private PublicKey receiverPk;

    //REGS
    private int rid;
    private int wts;


    public Func getFunction() {
        return function;
    }

    public void setFunction(Func function) {
        this.function = function;
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


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getSign() {
        return sign;
    }

    public void setSign(byte[] signature) {
        this.sign = signature;
    }

}
