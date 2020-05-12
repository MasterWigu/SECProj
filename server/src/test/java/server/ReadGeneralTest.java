package server;

import commonClasses.Announcement;
import commonClasses.MessageSigner;
import commonClasses.User;
import commonClasses.exceptions.*;
import library.Packet;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class ReadGeneralTest extends ServerTestsBase {
    private Announcement ann1;

    @BeforeMethod
    public void populateServer() throws UserNotFoundException, KeyException, AnnouncementNotFoundException, InvalidAnnouncementException, InvalidWtsException {
        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(1, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        ann1 = new Announcement("ANN01".toCharArray(), new User(id, client1Keys.getPublic()), null, 1);
        ann1.setWts(wts+1);
        MessageSigner.sign(ann1, client1Keys.getPrivate());
        String out = server.postGeneral(client1Keys.getPublic(), ann1, wts+1);
        String id1 = out.replace("Announcement successfully posted with id ", "").replace(" to general board.", "");

        Packet p = new Packet();
        Announcement a1 = server.getAnnouncementById(id1.toCharArray(), p);
        Assert.assertEquals(p.getWts(), 1);
    }


    @Test
    public void readAll1() {
        Packet p = new Packet();
        Assert.assertEquals(server.readGeneral(p).get(ann1.getCreator().getId()).size(),1);
        Assert.assertEquals(p.getWts(), 1);
        Assert.assertEquals(ann1, server.readGeneral(p).get(ann1.getCreator().getId()).get(0));
        Assert.assertEquals(p.getWts(), 1);
    }

    @Test
    public void readAll2() throws UserNotFoundException, AnnouncementNotFoundException, KeyException, InvalidAnnouncementException, InvalidWtsException {
        int uid2 = Integer.parseInt(server.register(client2Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(1, client2Keys.getPublic());
        Assert.assertEquals(wts, 1);

        Announcement ann = new Announcement("ANN02".toCharArray(), new User(uid2, client2Keys.getPublic()), null, 1);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client2Keys.getPrivate());
        server.postGeneral(client2Keys.getPublic(), ann, wts+1);

        Packet p = new Packet();
        Assert.assertEquals(server.readGeneral(p).get(ann1.getCreator().getId()).size(),1);
        Assert.assertEquals(p.getWts(), 2);
        Assert.assertEquals(server.readGeneral(p).get(ann.getCreator().getId()).size(), 1);
        Assert.assertEquals(p.getWts(), 2);
        Assert.assertEquals(server.readGeneral(p).get(ann1.getCreator().getId()).get(0), ann1);
        Assert.assertEquals(p.getWts(), 2);
        Assert.assertEquals(server.readGeneral(p).get(ann.getCreator().getId()).get(0), ann);
        Assert.assertEquals(p.getWts(), 2);
    }

    @Test
    public void read1of2() throws UserNotFoundException, AnnouncementNotFoundException, KeyException, InvalidAnnouncementException, InvalidWtsException {
        int uid2 = Integer.parseInt(server.register(client2Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(1, client2Keys.getPublic());
        Assert.assertEquals(wts, 1);

        Announcement ann = new Announcement("ANN02".toCharArray(), new User(uid2, client2Keys.getPublic()), new Announcement[] {ann1}, 1);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client2Keys.getPrivate());

        Announcement ann2 = new Announcement("ANN02".toCharArray(), new User(uid2, client2Keys.getPublic()), new Announcement[] {ann1}, 1);
        ann2.setWts(server.getChannelWts(1, client2Keys.getPublic())+1);
        MessageSigner.sign(ann2, client2Keys.getPrivate());

        Packet p = new Packet();
        server.postGeneral(client2Keys.getPublic(), ann, wts+1);
        Assert.assertEquals(server.readGeneral(p).get(ann1.getCreator().getId()).size(),1);
        Assert.assertEquals(ann1, server.readGeneral(p).get(ann1.getCreator().getId()).get(0));
    }

    @Test
    public void readAllWithReffs() throws UserNotFoundException, AnnouncementNotFoundException, KeyException, InvalidAnnouncementException, InvalidWtsException {
        int uid2 = Integer.parseInt(server.register(client2Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(1, client2Keys.getPublic());
        Assert.assertEquals(wts, 1);

        Announcement ann = new Announcement("ANN02".toCharArray(), new User(uid2, client2Keys.getPublic()), new Announcement[] {ann1}, 1);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client2Keys.getPrivate());
        server.postGeneral(client2Keys.getPublic(), ann, wts+1);


        Packet p = new Packet();
        Assert.assertEquals(server.readGeneral(p).get(ann1.getCreator().getId()).size(),1);
        Assert.assertEquals(p.getWts(), 2);
        Assert.assertEquals(server.readGeneral(p).get(ann.getCreator().getId()).size(), 1);
        Assert.assertEquals(p.getWts(), 2);
        Assert.assertEquals(server.readGeneral(p).get(ann1.getCreator().getId()).get(0), ann1);
        Assert.assertEquals(p.getWts(), 2);
        Assert.assertEquals(server.readGeneral(p).get(ann.getCreator().getId()).get(0), ann);
        Assert.assertEquals(p.getWts(), 2);
    }
}
