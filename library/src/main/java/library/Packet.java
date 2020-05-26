package library;

import commonClasses.Announcement;
import commonClasses.User;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class Packet implements Serializable {
    private static final Random random = new Random();

    public char[] getCharId() {
        return charId;
    }

    void setCharId(char[] charId) {
        this.charId = charId;
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
    private Func auxFunction;
    private Map<Integer, ArrayList<Announcement>> announcements;
    private User user;
    private char[] msg;

    private int id;
    private char[] charId;
    private Announcement singleAnnouncement;

    //INTEGRITY
    private Integer nonce;
    private byte[] sign = null;

    //APP2PL
    private PublicKey senderPk;
    private PublicKey receiverPk;

    //REGS
    private int rid;
    private int wts;




    public Packet() {
        this.nonce = random.nextInt();
        this.id = -1;
    }

    Integer getNonce() {
        return nonce;
    }

    public void setNonce() {
        this.nonce = random.nextInt();
    }

    PublicKey getReceiverPk() {
        return receiverPk;
    }

    void setReceiverPk(PublicKey receiverPk) {
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

    public Announcement getSingleAnnouncement() {
        return singleAnnouncement;
    }

    public void setSingleAnnouncement(Announcement singleAnnouncement) {
        this.singleAnnouncement = singleAnnouncement;
    }

    public Map<Integer, ArrayList<Announcement>> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(Map<Integer, ArrayList<Announcement>> announcements) {
        this.announcements = announcements;
    }

    public char[] getMessage() {
        return msg;
    }

    void setMessage(char[] msg) {
        this.msg = msg;
    }

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

    PublicKey getSenderPk() {
        return senderPk;
    }

    void setSenderPk(PublicKey key) {
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

    Func getAuxFunction() {
        return auxFunction;
    }

    public void setAuxFunction(Func auxFunction) {
        this.auxFunction = auxFunction;
    }


}
