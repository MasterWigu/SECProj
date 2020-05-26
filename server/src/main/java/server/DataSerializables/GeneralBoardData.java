package server.DataSerializables;

import commonClasses.Announcement;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class GeneralBoardData implements Serializable {
    private HashMap<Integer, ArrayList<Announcement>> generalBoard;
    private int generalWts;

    public GeneralBoardData(HashMap<Integer, ArrayList<Announcement>> generalBoard, Integer generalWts) {
        this.generalBoard = SerializationUtils.clone(generalBoard);
        this.generalWts = generalWts;
    }

    public GeneralBoardData() {
        this.generalBoard = new HashMap<>();
        this.generalWts = 0;
    }

    public HashMap<Integer, ArrayList<Announcement>> getGeneralBoard() {
        return generalBoard;
    }

    public int getGeneralWts() {
        return generalWts;
    }
}
