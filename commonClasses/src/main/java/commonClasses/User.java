package commonClasses;

import java.io.Serializable;
import java.security.PublicKey;

public class User implements Serializable {

    private int id;
    private PublicKey publicKey;

    public User(int uid, PublicKey pk) {
        id = uid;
        publicKey = pk;
    }

    public PublicKey getPk() {
        return publicKey;
    }


    public int getId() {
        return id;
    }
}
