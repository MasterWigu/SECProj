package commonClasses;

import java.io.Serializable;
import java.security.PublicKey;

public class User implements Serializable {

    private PublicKey publicKey;
    private String username;

    public User(PublicKey pk, String uname) {
        publicKey = pk;
        username = uname;
    }

    public PublicKey getPk() {
        return publicKey;
    }

    public String getUsername() {
        return username;
    }

}
