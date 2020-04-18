package library.Algorithms;

import library.Packet;

import java.util.ArrayList;
import java.util.List;

public class BRegularRegister {

    private int wts;
    private int rid;

    private List<Packet> ackList;
    private List<Packet> readList;



    public BRegularRegister() {
        wts = 0;
        rid = 0;

        ackList = new ArrayList<>();
        readList = new ArrayList<>();

    }

    public Packet write(Packet pack) {

        wts++;
        ackList = new ArrayList<>();
        return null;
    }
}
