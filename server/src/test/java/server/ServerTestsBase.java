package server;

import commonClasses.Announcement;
import commonClasses.MessageSigner;
import commonClasses.User;
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
    DPASService server;

    String createAnn(KeyPair clientKeys, char[] mss, Announcement[] anns, int wts, User creator) throws UserNotFoundException, InvalidAnnouncementException {
        Announcement ann = new Announcement(mss, creator, anns, 0);
        ann.setWts(wts);
        MessageSigner.sign(ann, clientKeys.getPrivate());
        String out = server.post(clientKeys.getPublic(), ann, wts);

        String out2 = out.replace("Announcement successfully posted with id ", "").replace(" to personal board.", "");
        return out2;
    }

    String createGenAnn(KeyPair clientKeys, char[] mss, Announcement[] anns, int wts, User creator) throws UserNotFoundException, InvalidAnnouncementException {
        Announcement ann = new Announcement(mss, creator, anns, 1);
        ann.setWts(wts);
        MessageSigner.sign(ann, clientKeys.getPrivate());
        String out = server.postGeneral(clientKeys.getPublic(), ann, wts);

        String out2 = out.replace("Announcement successfully posted with id ", "").replace(" to general board", "");
        return out2;
    }

    @BeforeMethod
    public void setUp() {
        serverKeys = KeyStoreCreator.createKeyPair();
        client1Keys = KeyStoreCreator.createKeyPair();
        client2Keys = KeyStoreCreator.createKeyPair();
        client3Keys = KeyStoreCreator.createKeyPair();

        server = new DPASService(false, true);

    }
}
