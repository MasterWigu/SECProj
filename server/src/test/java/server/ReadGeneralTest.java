package server;

import commonClasses.Announcement;
import commonClasses.MessageSigner;
import commonClasses.exceptions.AnnouncementNotFoundException;
import commonClasses.exceptions.InvalidAnnouncementException;
import commonClasses.exceptions.KeyException;
import commonClasses.exceptions.UserNotFoundException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class ReadGeneralTest extends ServerTestsBase {
    private Announcement ann1;


    @BeforeMethod
    public void populateServer() throws UserNotFoundException, KeyException, AnnouncementNotFoundException, InvalidAnnouncementException {
        server.register(client1Keys.getPublic(), "TESTU01");

        long time = System.currentTimeMillis();
        byte[] sign1 = MessageSigner.sign("ANN01".toCharArray(), client1Keys.getPublic(), 1, null, client1Keys.getPrivate());
        String out = server.postGeneral(client1Keys.getPublic(), "ANN01".toCharArray(), null, time, sign1);

        String out2 = out.replace("Announcement successfully posted with id ", "").replace(" to general board", "");
        int id = Integer.parseInt(out2);
        ann1 = server.getAnnouncementById(id);

    }


    @Test
    public void readAll1() {
        Assert.assertEquals(1, server.readGeneral(0).length);
        Assert.assertEquals(ann1, server.readGeneral(0)[0]);
    }

    @Test
    public void readAll2() throws UserNotFoundException, AnnouncementNotFoundException, KeyException, InvalidAnnouncementException {
        server.register(client2Keys.getPublic(), "TEST2");
        int id1 = createGenAnn(client2Keys, "ANN02".toCharArray(), null);
        Announcement a1 = server.getAnnouncementById(id1);

        Assert.assertEquals(2, server.readGeneral(0).length);
        Assert.assertEquals(ann1, server.readGeneral(0)[0]);
        Assert.assertEquals(a1, server.readGeneral(0)[1]);
    }

    @Test
    public void read1of2() throws UserNotFoundException, AnnouncementNotFoundException, KeyException, InvalidAnnouncementException {
        server.register(client2Keys.getPublic(), "TEST2");
        int id1 = createGenAnn(client2Keys, "ANN02".toCharArray(), null);
        Announcement a1 = server.getAnnouncementById(id1);

        Assert.assertEquals(1, server.readGeneral(1).length);
        Assert.assertEquals(ann1, server.readGeneral(1)[0]);
    }

    @Test
    public void readAllWithReffs() throws UserNotFoundException, AnnouncementNotFoundException, KeyException, InvalidAnnouncementException {
        server.register(client2Keys.getPublic(), "TEST2");
        int id1 = createGenAnn(client2Keys, "ANN02".toCharArray(), new Announcement[] {ann1});
        Announcement a1 = server.getAnnouncementById(id1);

        Assert.assertEquals(2, server.readGeneral(0).length);
        Assert.assertEquals(ann1, server.readGeneral(0)[0]);
        Assert.assertEquals(a1, server.readGeneral(0)[1]);

    }


}
