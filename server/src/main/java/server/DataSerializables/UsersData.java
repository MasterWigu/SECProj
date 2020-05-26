package server.DataSerializables;

import commonClasses.User;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;


public class UsersData implements Serializable {
    private ArrayList<User> users;
    private int registerWts;

    public UsersData(ArrayList<User> users, Integer registerWts) {
        this.users = SerializationUtils.clone(users);
        this.registerWts = registerWts;
    }

    public UsersData() {
        this.users = new ArrayList<>();
        this.registerWts = 0;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public int getRegisterWts() {
        return registerWts;
    }
}
