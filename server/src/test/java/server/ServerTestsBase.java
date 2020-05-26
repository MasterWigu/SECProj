package server;

import keyStoreCreator.KeyStoreCreator;
import org.testng.annotations.BeforeMethod;

import java.security.KeyPair;

public class ServerTestsBase {
    KeyPair client1Keys;
    KeyPair client2Keys;
    KeyPair client3Keys;
    DPASService server;

    @BeforeMethod
    public void setUp() {
        client1Keys = KeyStoreCreator.createKeyPair();
        client2Keys = KeyStoreCreator.createKeyPair();
        client3Keys = KeyStoreCreator.createKeyPair();

        server = new DPASService(false, true);

    }
}
