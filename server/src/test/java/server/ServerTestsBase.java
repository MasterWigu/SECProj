package server;

import keyStoreCreator.KeyStoreCreator;
import org.junit.Before;

import java.security.KeyPair;

public class ServerTestsBase {
    KeyPair serverKeys;
    KeyPair client1Keys;
    KeyPair client2Keys;
    KeyPair client3Keys;
    DPASService server;



    @Before
    public void setUp() {
        serverKeys = KeyStoreCreator.createKeyPair();
        client1Keys = KeyStoreCreator.createKeyPair();
        client2Keys = KeyStoreCreator.createKeyPair();
        client3Keys = KeyStoreCreator.createKeyPair();

        server = new DPASService();

    }
}
