package server;

import commonClasses.Announcement;
import commonClasses.MessageSigner;
import commonClasses.exceptions.InvalidAnnouncementException;
import commonClasses.exceptions.UserNotFoundException;
import keyStoreCreator.KeyStoreCreator;
import org.testng.annotations.BeforeMethod;
import java.security.KeyPair;

public class ServerTestsBase {
    KeyPair serverKeys;
    KeyPair client1Keys;
    KeyPair client2Keys;
    KeyPair client3Keys;
    DPASService server;/*

    int createAnn(KeyPair clientKeys, char[] mss, Announcement[] anns) throws UserNotFoundException, InvalidAnnouncementException {
        long time = System.currentTimeMillis();
        byte[] sign1 = MessageSigner.sign(mss, clientKeys.getPublic(), 0, anns, clientKeys.getPrivate());
        String out = server.post(clientKeys.getPublic(), mss, anns, time, sign1);

        String out2 = out.replace("Announcement successfully posted with id ", "").replace(" to personal board.", "");
        return Integer.parseInt(out2);
    }

    int createGenAnn(KeyPair clientKeys, char[] mss, Announcement[] anns) throws UserNotFoundException, InvalidAnnouncementException {
        long time = System.currentTimeMillis();
        byte[] sign1 = MessageSigner.sign(mss, clientKeys.getPublic(), 1, anns, clientKeys.getPrivate());
        String out = server.postGeneral(clientKeys.getPublic(), mss, anns, time, sign1);

        String out2 = out.replace("Announcement successfully posted with id ", "").replace(" to general board", "");
        return Integer.parseInt(out2);
    }

    @BeforeMethod
    public void setUp() {
        serverKeys = KeyStoreCreator.createKeyPair();
        client1Keys = KeyStoreCreator.createKeyPair();
        client2Keys = KeyStoreCreator.createKeyPair();
        client3Keys = KeyStoreCreator.createKeyPair();

        server = new DPASService(false, true);

    }*/
}
