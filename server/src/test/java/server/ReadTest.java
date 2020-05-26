package server;

import commonClasses.Announcement;
import commonClasses.MessageSigner;
import commonClasses.User;
import commonClasses.exceptions.*;
import library.Packet;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ReadTest extends ServerTestsBase {
    private Announcement ann1;


    @BeforeMethod
    public void populateServer() throws InvalidWtsException, UserNotFoundException, KeyException, AnnouncementNotFoundException, InvalidAnnouncementException {
        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts()+1, new Packet()).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(0, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id, client1Keys.getPublic()), null, 0);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client1Keys.getPrivate());
        String out = server.post(client1Keys.getPublic(), ann, wts+1);
        String out2 = out.replace("Announcement successfully posted with id ", "").replace(" to personal board.", "");
        Packet p = new Packet();
        ann1 = server.getAnnouncementById(out2.toCharArray(), p);
        Assert.assertEquals(p.getWts(), 1);
    }


    @Test
    public void readAll1() throws UserNotFoundException {
        Packet p = new Packet();
        Assert.assertEquals(server.read(client1Keys.getPublic(),p).get(-1).size(), 1);
        Assert.assertEquals(p.getWts(), 1);
        Assert.assertEquals(ann1, server.read(client1Keys.getPublic(),p).get(-1).get(0));
        Assert.assertEquals(p.getWts(), 1);
    }


    @Test
    public void readAll2() throws InvalidWtsException, UserNotFoundException, KeyException, InvalidAnnouncementException {
        int uid2 = Integer.parseInt(server.register(client2Keys.getPublic(), server.getRegisterWts()+1, new Packet()).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(0, client2Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN02".toCharArray(), new User(uid2, client2Keys.getPublic()), null, 0);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client2Keys.getPrivate());
        server.post(client2Keys.getPublic(), ann, wts+1);

        Packet p = new Packet();
        Assert.assertEquals(server.read(client1Keys.getPublic(), p).get(-1).size(),1);
        Assert.assertEquals(p.getWts(), 1);
        Assert.assertEquals(server.read(client2Keys.getPublic(), p).get(-1).size(), 1);
        Assert.assertEquals(p.getWts(), 1);
        Assert.assertEquals(server.read(client1Keys.getPublic(),p).get(-1).get(0), ann1);
        Assert.assertEquals(p.getWts(), 1);
        Assert.assertEquals(server.read(client2Keys.getPublic(),p).get(-1).get(0), ann);
        Assert.assertEquals(p.getWts(), 1);
    }


    @Test
    public void readAllWithReffs() throws InvalidWtsException, UserNotFoundException, KeyException, InvalidAnnouncementException {
        int uid2 = Integer.parseInt(server.register(client2Keys.getPublic(), server.getRegisterWts()+1, new Packet()).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(0, client2Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN02".toCharArray(), new User(uid2, client2Keys.getPublic()), new Announcement[] {ann1}, 0);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client2Keys.getPrivate());
        server.post(client2Keys.getPublic(), ann, wts+1);


        Packet p = new Packet();
        Assert.assertEquals(server.read(client1Keys.getPublic(), p).get(-1).size(),1);
        Assert.assertEquals(p.getWts(), 1);
        Assert.assertEquals(server.read(client2Keys.getPublic(), p).get(-1).size(), 1);
        Assert.assertEquals(p.getWts(), 1);
        Assert.assertEquals(server.read(client1Keys.getPublic(),p).get(-1).get(0), ann1);
        Assert.assertEquals(p.getWts(), 1);
        Assert.assertEquals(server.read(client2Keys.getPublic(),p).get(-1).get(0), ann);
        Assert.assertEquals(p.getWts(), 1);

    }


}
