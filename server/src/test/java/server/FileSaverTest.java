package server;

import commonClasses.Announcement;
import commonClasses.User;
import keyStoreCreator.KeyStoreCreator;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

public class FileSaverTest extends AnnUserCmp {

    private Announcement ann1;
    private Announcement ann2;
    private Announcement ann3;
    private User u1;
    private User u2;
    private User u3;
    private FileSaver f;

    @BeforeSuite
    public void setUp() {
        KeyPair k1 = KeyStoreCreator.createKeyPair();
        KeyPair k2 = KeyStoreCreator.createKeyPair();
        KeyPair k3 = KeyStoreCreator.createKeyPair();

        u1 = new User(1, k1.getPublic(), "");
        u2 = new User(2, k2.getPublic(), "");
        u3 = new User(3, k3.getPublic(), "");

        ann1 = new Announcement("ANN1".toCharArray(), u1, null, 0, 0, null);
        ann2 = new Announcement("ANN2".toCharArray(), u2, new Announcement[]{ann1}, 10, 0, null);
        ann3 = new Announcement("ANN3".toCharArray(), u3, null, 0, 20, null);

        f = FileSaver.getInstance("src\\test\\resources\\", 1);
    }

    @Test
    public void writeReadUsers1() {
        List<User> us = new ArrayList<>();
        us.add(u1);
        f.writeUsers(us);

        List<User> uRead = f.readUsers();
        for (int i = 0; i<uRead.size(); i++) {
            userCmp(us.get(i), uRead.get(i));
        }
    }

    @Test
    public void writeReadUsers3() {
        List<User> us = new ArrayList<>();
        us.add(u1);
        us.add(u2);
        us.add(u3);
        f.writeUsers(us);

        List<User> uRead = f.readUsers();
        for (int i = 0; i<uRead.size(); i++) {
            userCmp(us.get(i), uRead.get(i));
        }
    }

    @Test
    public void writeReadAnns1() {
        List<Announcement> as = new ArrayList<>();
        as.add(ann1);
        f.writeAnnouncements(as);

        List<Announcement> aRead = f.readAnnouncements();
        for (int i = 0; i<aRead.size(); i++) {
            annCmp(as.get(i), aRead.get(i));
        }

    }

    @Test
    public void writeReadAnns3() {
        List<Announcement> as = new ArrayList<>();
        as.add(ann1);
        as.add(ann2);
        as.add(ann3);

        f.writeAnnouncements(as);

        List<Announcement> aRead = f.readAnnouncements();
        for (int i = 0; i<aRead.size(); i++) {
            annCmp(as.get(i), aRead.get(i));
        }
    }


}
