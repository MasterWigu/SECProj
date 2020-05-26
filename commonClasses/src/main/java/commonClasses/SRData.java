package commonClasses;

import java.security.PrivateKey;
import java.security.PublicKey;

public class SRData {
    private String host;
    private int port;
    private PublicKey pubKey;
    private PrivateKey prvKey;
    private int id;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public PublicKey getPubKey() {
        return pubKey;
    }

    public void setPubKey(PublicKey pubKey) {
        this.pubKey = pubKey;
    }

    public PrivateKey getPrvKey() {
        return prvKey;
    }

    public void setPrvKey(PrivateKey prvKey) {
        this.prvKey = prvKey;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        String out = "-----SRData-----\n";
        out += "    Id: "  +id   + "\n";
        out += "    Host: "+host + "\n";
        out += "    Port: "+port + "\n";

        out += "    PubK: "+pubKey + "\n";
        out += "    PrvK: "+prvKey + "\n";
        return out;
    }
}
