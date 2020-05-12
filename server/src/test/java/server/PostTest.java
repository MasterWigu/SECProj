package server;

import commonClasses.Announcement;
import commonClasses.MessageSigner;
import commonClasses.User;
import commonClasses.exceptions.*;
import library.Packet;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class PostTest extends ServerTestsBase {



    @Test
    public void success() throws InvalidWtsException, UserNotFoundException, KeyException, InvalidAnnouncementException {

        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(0, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id, client1Keys.getPublic()), null, 0);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client1Keys.getPrivate());
        String out = server.post(client1Keys.getPublic(), ann, wts+1);
        String out2 = out.replace("Announcement successfully posted with id ", "").replace(" to personal board.", "");

        Packet p = new Packet();
        Announcement a = server.read(client1Keys.getPublic(), p).get(-1).get(0);
        int wts2 = server.getChannelWts(0, client1Keys.getPublic());

        AssertJUnit.assertArrayEquals(out2.toCharArray(), a.getId());
        AssertJUnit.assertArrayEquals(a.getMessage(), "ANN01".toCharArray());
        Assert.assertEquals(a.getCreator().getPk(), client1Keys.getPublic());
        Assert.assertEquals(a.getBoard(), 0);
        Assert.assertNull(a.getRefs());
        Assert.assertEquals(a.getWts(), wts+1);
        Assert.assertEquals(wts2, 1);
    }

    @Test
    public void successWithReffs() throws InvalidWtsException, UserNotFoundException, AnnouncementNotFoundException, KeyException, InvalidAnnouncementException {

        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(0, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id, client1Keys.getPublic()), null, 0);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client1Keys.getPrivate());
        String out = server.post(client1Keys.getPublic(), ann, wts+1);
        String id1 = out.replace("Announcement successfully posted with id ", "").replace(" to personal board.", "");

        Packet p = new Packet();
        Announcement a1 = server.getAnnouncementById(id1.toCharArray(), p);
        Assert.assertEquals(p.getWts(), 1);



        int wts2 = server.getChannelWts(0, client1Keys.getPublic());
        Assert.assertEquals(wts2, 1);

        Announcement ann2 = new Announcement("ANN02".toCharArray(), new User(id, client1Keys.getPublic()), new Announcement[] {a1}, 0);
        ann2.setWts(wts2+1);
        MessageSigner.sign(ann2, client1Keys.getPrivate());
        String out2 = server.post(client1Keys.getPublic(), ann2, wts2+1);
        String id2 = out2.replace("Announcement successfully posted with id ", "").replace(" to personal board.", "");

        Packet p2 = new Packet();
        Announcement a2 = server.getAnnouncementById(id2.toCharArray(), p2);
        Assert.assertEquals(p2.getWts(), 2);


        AssertJUnit.assertArrayEquals(a1.getMessage(), "ANN01".toCharArray());
        Assert.assertEquals(a1.getCreator().getPk(), client1Keys.getPublic());
        Assert.assertEquals(a1.getBoard(), 0);
        Assert.assertNull(a1.getRefs());
        Assert.assertEquals(a1.getWts(), wts+1);
        Assert.assertTrue(MessageSigner.verify(a1));
        Assert.assertTrue(MessageSigner.verify(a1, client1Keys.getPublic()));

        AssertJUnit.assertArrayEquals(a2.getMessage(), "ANN02".toCharArray());
        Assert.assertEquals(a2.getCreator().getPk(), client1Keys.getPublic());
        Assert.assertEquals(a2.getBoard(), 0);
        Assert.assertNotNull(a2.getRefs());
        AssertJUnit.assertArrayEquals(a2.getRefs(), new Announcement[]{a1});
        Assert.assertEquals(a2.getWts(), wts2+1);
        Assert.assertTrue(MessageSigner.verify(a2));
        Assert.assertTrue(MessageSigner.verify(a2, client1Keys.getPublic()));

    }

    @Test
    public void successWithReffsDiffUsers() throws InvalidWtsException, UserNotFoundException, AnnouncementNotFoundException, KeyException, InvalidAnnouncementException {

        int uid1 = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));
        int uid2 = Integer.parseInt(server.register(client2Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(0, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(uid1, client1Keys.getPublic()), null, 0);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client1Keys.getPrivate());
        String out = server.post(client1Keys.getPublic(), ann, wts+1);
        String id1 = out.replace("Announcement successfully posted with id ", "").replace(" to personal board.", "");

        Packet p = new Packet();
        Announcement a1 = server.getAnnouncementById(id1.toCharArray(), p);
        Assert.assertEquals(p.getWts(), 1);



        int wts2 = server.getChannelWts(0, client2Keys.getPublic());
        Assert.assertEquals(wts2, 0);

        Announcement ann2 = new Announcement("ANN02".toCharArray(), new User(uid2, client2Keys.getPublic()), new Announcement[] {a1}, 0);
        ann2.setWts(wts2+1);
        MessageSigner.sign(ann2, client2Keys.getPrivate());
        String out2 = server.post(client2Keys.getPublic(), ann2, wts2+1);
        String id2 = out2.replace("Announcement successfully posted with id ", "").replace(" to personal board.", "");

        Packet p2 = new Packet();
        Announcement a2 = server.getAnnouncementById(id2.toCharArray(), p2);
        Assert.assertEquals(p2.getWts(), 1);


        AssertJUnit.assertArrayEquals(a1.getMessage(), "ANN01".toCharArray());
        Assert.assertEquals(a1.getCreator().getPk(), client1Keys.getPublic());
        Assert.assertEquals(a1.getBoard(), 0);
        Assert.assertNull(a1.getRefs());
        Assert.assertEquals(a1.getWts(), wts+1);
        Assert.assertTrue(MessageSigner.verify(a1));
        Assert.assertTrue(MessageSigner.verify(a1, client1Keys.getPublic()));

        AssertJUnit.assertArrayEquals(a2.getMessage(), "ANN02".toCharArray());
        Assert.assertEquals(a2.getCreator().getPk(), client2Keys.getPublic());
        Assert.assertEquals(a2.getBoard(), 0);
        Assert.assertNotNull(a2.getRefs());
        AssertJUnit.assertArrayEquals(a2.getRefs(), new Announcement[]{a1});
        Assert.assertEquals(a2.getWts(), wts2+1);
        Assert.assertTrue(MessageSigner.verify(a2));
        Assert.assertTrue(MessageSigner.verify(a2, client2Keys.getPublic()));

        Packet p3 = new Packet();
        Assert.assertEquals(a1, server.read(client1Keys.getPublic(),p3).get(-1).get(0));
        Assert.assertEquals(p.getWts(), wts+1);
        Assert.assertEquals(a2, server.read(client2Keys.getPublic(),p3).get(-1).get(0));
        Assert.assertEquals(p.getWts(), wts2+1);
    }

    @Test(expectedExceptions = UserNotFoundException.class)
    public void userNotRegisteredWts() throws UserNotFoundException {
        server.getChannelWts(0, client1Keys.getPublic());
    }

    @Test(expectedExceptions = UserNotFoundException.class)
    public void userNotRegistered() throws UserNotFoundException, InvalidAnnouncementException {
        Announcement ann = new Announcement("ANN01".toCharArray(), new User(0, client1Keys.getPublic()), null, 0);
        ann.setWts(1);
        MessageSigner.sign(ann, client1Keys.getPrivate());
        server.post(client1Keys.getPublic(), ann, 1);
    }


    @Test(expectedExceptions = InvalidAnnouncementException.class)
    public void invalidSignatureMess() throws InvalidWtsException, UserNotFoundException, KeyException, InvalidAnnouncementException {

        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(0, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id, client1Keys.getPublic()), null, 0);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client1Keys.getPrivate());
        ann.setWts(-1); //changing any field

        server.post(client1Keys.getPublic(), ann, wts+1);
    }


    @Test(expectedExceptions = InvalidAnnouncementException.class)
    public void invalidSignaturePuk1() throws InvalidWtsException, UserNotFoundException, KeyException, InvalidAnnouncementException {

        int id1 = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));
        server.register(client2Keys.getPublic(), server.getRegisterWts()+1);

        int wts = server.getChannelWts(0, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id1, client1Keys.getPublic()), null, 0);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client2Keys.getPrivate());

        server.post(client1Keys.getPublic(), ann, wts+1);
    }


    @Test(expectedExceptions = InvalidAnnouncementException.class)
    public void invalidSignaturePuk2() throws InvalidWtsException, UserNotFoundException, KeyException, InvalidAnnouncementException {

        int id1 = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));
        server.register(client2Keys.getPublic(), server.getRegisterWts()+1);

        int wts = server.getChannelWts(0, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id1, client1Keys.getPublic()), null, 0);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client1Keys.getPrivate());

        server.post(client2Keys.getPublic(), ann, wts+1);
    }


    @Test(expectedExceptions = InvalidAnnouncementException.class)
    public void invalidSignatureBoard() throws InvalidWtsException, UserNotFoundException, KeyException, InvalidAnnouncementException {

        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(0, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id, client1Keys.getPublic()), null, 1);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client1Keys.getPrivate());
        server.post(client1Keys.getPublic(), ann, wts+1);
    }


    @Test(expectedExceptions = InvalidAnnouncementException.class)
    public void invalidSignatureReffs() throws InvalidWtsException, UserNotFoundException, AnnouncementNotFoundException, KeyException, InvalidAnnouncementException {
        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(0, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id, client1Keys.getPublic()), null, 0);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client1Keys.getPrivate());
        String out = server.post(client1Keys.getPublic(), ann, wts+1);
        String id1 = out.replace("Announcement successfully posted with id ", "").replace(" to personal board.", "");

        Packet p = new Packet();
        Announcement a1 = server.getAnnouncementById(id1.toCharArray(), p);
        Assert.assertEquals(p.getWts(), 1);



        int wts2 = server.getChannelWts(0, client1Keys.getPublic());
        Assert.assertEquals(wts2, 1);

        Announcement ann2 = new Announcement("ANN02".toCharArray(), new User(id, client1Keys.getPublic()), null, 0);
        ann2.setWts(wts2+1);
        MessageSigner.sign(ann2, client1Keys.getPrivate());
        Announcement ann3 = new Announcement("ANN02".toCharArray(), new User(id, client1Keys.getPublic()), new Announcement[] {a1}, 0);
        ann3.setSignature(ann2.getSignature());
        server.post(client1Keys.getPublic(), ann3, wts2+1);
    }


    @Test
    public void duplicateAnnouncement() throws InvalidWtsException, UserNotFoundException, KeyException, InvalidAnnouncementException {
        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(0, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id, client1Keys.getPublic()), null, 0);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client1Keys.getPrivate());
        server.post(client1Keys.getPublic(), ann, wts+1);
        String out = server.post(client1Keys.getPublic(), ann, wts+1);

        Assert.assertEquals("Duplicate Announcement", out);
    }

    @Test (expectedExceptions = InvalidAnnouncementException.class)
    public void wtsMismatch() throws InvalidWtsException, UserNotFoundException, KeyException, InvalidAnnouncementException {
        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts() + 1).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(0, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id, client1Keys.getPublic()), null, 0);
        ann.setWts(wts);
        MessageSigner.sign(ann, client1Keys.getPrivate());
        server.post(client1Keys.getPublic(), ann, wts + 1);
    }

    @Test (expectedExceptions = InvalidAnnouncementException.class)
    public void invalidWts() throws InvalidWtsException, UserNotFoundException, KeyException, InvalidAnnouncementException {
        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts() + 1).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(0, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id, client1Keys.getPublic()), null, 0);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client1Keys.getPrivate());
        server.post(client1Keys.getPublic(), ann, -1);
    }


    @Test
    public void successBuffer() throws InvalidWtsException, UserNotFoundException, AnnouncementNotFoundException, KeyException, InvalidAnnouncementException {

        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(0, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id, client1Keys.getPublic()), null, 0);
        ann.setWts(wts+2);
        MessageSigner.sign(ann, client1Keys.getPrivate());
        String out = server.post(client1Keys.getPublic(), ann, wts+2);
        String id1 = out.replace("Announcement successfully posted with id ", "").replace(" to personal board.", "");

        Assert.assertEquals(server.getChannelWts(0, client1Keys.getPublic()), 0);



        int wts2 = server.getChannelWts(0, client1Keys.getPublic());
        Assert.assertEquals(wts2, 0);

        Announcement ann2 = new Announcement("ANN02".toCharArray(), new User(id, client1Keys.getPublic()), null, 0);
        ann2.setWts(wts2+1);
        MessageSigner.sign(ann2, client1Keys.getPrivate());
        String out2 = server.post(client1Keys.getPublic(), ann2, wts2+1);
        String id2 = out2.replace("Announcement successfully posted with id ", "").replace(" to personal board.", "");

        Packet p2 = new Packet();
        Announcement a2 = server.getAnnouncementById(id2.toCharArray(), p2);
        Assert.assertEquals(p2.getWts(), 2);

        Packet p = new Packet();
        Announcement a1 = server.getAnnouncementById(id1.toCharArray(), p);


        AssertJUnit.assertArrayEquals(a1.getMessage(), "ANN01".toCharArray());
        Assert.assertEquals(a1.getCreator().getPk(), client1Keys.getPublic());
        Assert.assertEquals(a1.getBoard(), 0);
        Assert.assertNull(a1.getRefs());
        Assert.assertEquals(a1.getWts(), wts+2);
        Assert.assertTrue(MessageSigner.verify(a1));
        Assert.assertTrue(MessageSigner.verify(a1, client1Keys.getPublic()));

        AssertJUnit.assertArrayEquals(a2.getMessage(), "ANN02".toCharArray());
        Assert.assertEquals(a2.getCreator().getPk(), client1Keys.getPublic());
        Assert.assertEquals(a2.getBoard(), 0);
        Assert.assertNull(a2.getRefs());
        Assert.assertEquals(a2.getWts(), wts2+1);
        Assert.assertTrue(MessageSigner.verify(a2));
        Assert.assertTrue(MessageSigner.verify(a2, client1Keys.getPublic()));

    }

}
