package commonClasses;

import java.io.Serializable;
import java.security.PublicKey;

public class User implements Serializable {

    private int id;
    private PublicKey publicKey;
    private String username;

    public User(int uid, PublicKey pk, String uname) {
        id = uid;
        publicKey = pk;
        username = uname;
    }

    public PublicKey getPk() {
        return publicKey;
    }

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }
}
