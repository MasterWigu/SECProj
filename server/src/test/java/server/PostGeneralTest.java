package server;

import commonClasses.Announcement;
import commonClasses.MessageSigner;
import commonClasses.User;
import commonClasses.exceptions.*;
import library.Packet;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class PostGeneralTest extends ServerTestsBase {

    @Test
    public void success() throws UserNotFoundException, AnnouncementNotFoundException, KeyException, InvalidAnnouncementException, InvalidWtsException {
        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));
        int wts = server.getChannelWts(1, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id, client1Keys.getPublic()), null, 1);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client1Keys.getPrivate());
        String out = server.postGeneral(client1Keys.getPublic(), ann, wts+1);
        String out2 = out.replace("Announcement successfully posted with id ", "").replace(" to general board.", "");

        Packet p = new Packet();
        Announcement a = server.readGeneral(p).get(id).get(0);
        int wts2 = server.getChannelWts(1, client1Keys.getPublic());

        AssertJUnit.assertArrayEquals(out2.toCharArray(), a.getId());
        AssertJUnit.assertArrayEquals(a.getMessage(), "ANN01".toCharArray());
        Assert.assertEquals(a.getCreator().getPk(), client1Keys.getPublic());
        Assert.assertEquals(a.getBoard(), 1);
        Assert.assertNull(a.getRefs());
        Assert.assertEquals(a.getWts(), wts+1);
        Assert.assertEquals(wts2, 1);
    }

    @Test
    public void successWithReffs() throws UserNotFoundException, AnnouncementNotFoundException, KeyException, InvalidAnnouncementException, InvalidWtsException {
        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));
        int wts = server.getChannelWts(1, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        User u = new User(id, client1Keys.getPublic());
        Announcement ann = new Announcement("ANN01".toCharArray(), u, null, 1);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client1Keys.getPrivate());
        String out = server.postGeneral(client1Keys.getPublic(), ann, wts+1);
        String id1 = out.replace("Announcement successfully posted with id ", "").replace(" to general board.", "");

        Packet p = new Packet();
        Announcement a1 = server.getAnnouncementById(id1.toCharArray(), p);
        Assert.assertEquals(p.getWts(), 1);


        int wts2 = server.getChannelWts(1, client1Keys.getPublic());
        Assert.assertEquals(wts2, 1);

        Announcement ann2 = new Announcement("ANN02".toCharArray(), u, new Announcement[] {a1}, 1);
        ann2.setWts(wts2+1);
        MessageSigner.sign(ann2, client1Keys.getPrivate());
        String out2 = server.postGeneral(client1Keys.getPublic(), ann, wts2+1);
        String id2 = out2.replace("Announcement successfully posted with id ", "").replace(" to general board.", "");

        Packet p2 = new Packet();
        Announcement a2 = server.getAnnouncementById(id2.toCharArray(), p2);
        Assert.assertEquals(p2.getWts(), 2);

        AssertJUnit.assertArrayEquals(a1.getMessage(), "ANN01".toCharArray());
        Assert.assertEquals(a1.getCreator().getPk(), client1Keys.getPublic());
        Assert.assertEquals(a1.getBoard(), 1);
        Assert.assertNull(a1.getRefs());
        Assert.assertEquals(a1.getWts(), wts+1);
        Assert.assertTrue(MessageSigner.verify(a1));
        Assert.assertTrue(MessageSigner.verify(a1, client1Keys.getPublic()));

        // a2 throwing null pointer in toString TODO
        /*AssertJUnit.assertArrayEquals(a2.getMessage(), "ANN02".toCharArray());
        Assert.assertEquals(a2.getCreator().getPk(), client1Keys.getPublic());
        Assert.assertEquals(a2.getBoard(), 1);
        Assert.assertNotNull(a2.getRefs());
        AssertJUnit.assertArrayEquals(a2.getRefs(), new Announcement[]{a1});
        Assert.assertEquals(a2.getWts(), wts2+1);
        Assert.assertTrue(MessageSigner.verify(a2));
        Assert.assertTrue(MessageSigner.verify(a2, client1Keys.getPublic()));*/
    }

    @Test(expectedExceptions = UserNotFoundException.class)
    public void userNotRegistered() throws UserNotFoundException, InvalidAnnouncementException {
        server.postGeneral(client1Keys.getPublic(), new Announcement("ANN01".toCharArray(), new User(1, client1Keys.getPublic()), null, 1), 4);

    }

    @Test(expectedExceptions = InvalidAnnouncementException.class)
    public void invalidSignatureMess() throws UserNotFoundException, KeyException, InvalidAnnouncementException, InvalidWtsException {
        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));
        int wts = server.getChannelWts(1, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id, client1Keys.getPublic()), null, 0);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client2Keys.getPrivate());
        ann.setWts(-1);
        server.postGeneral(client1Keys.getPublic(), ann, wts+1);
    }

    @Test(expectedExceptions = InvalidAnnouncementException.class)
    public void invalidSignaturePuk1() throws UserNotFoundException, KeyException, InvalidAnnouncementException, InvalidWtsException {
        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));
        server.register(client2Keys.getPublic(), server.getRegisterWts()+1);

        int wts = server.getChannelWts(1, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id, client1Keys.getPublic()), null, 1);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client1Keys.getPrivate());
        server.postGeneral(client2Keys.getPublic(), ann, wts+1);
    }


    @Test(expectedExceptions = InvalidAnnouncementException.class)
    public void invalidSignaturePuk2() throws UserNotFoundException, KeyException, InvalidAnnouncementException, InvalidWtsException {
        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));
        server.register(client2Keys.getPublic(), server.getRegisterWts()+1);

        int wts = server.getChannelWts(1, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id, client1Keys.getPublic()), null, 1);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client2Keys.getPrivate());
        server.postGeneral(client1Keys.getPublic(), ann, wts+1);
    }


    @Test(expectedExceptions = InvalidAnnouncementException.class)
    public void invalidSignatureBoard() throws UserNotFoundException, KeyException, InvalidAnnouncementException, InvalidWtsException {
        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(1, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id, client1Keys.getPublic()), null, 0);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client1Keys.getPrivate());
        server.postGeneral(client1Keys.getPublic(), ann, wts+1);
    }


    /*@Test(expectedExceptions = InvalidAnnouncementException.class)
    public void invalidSignatureReffs() throws UserNotFoundException, AnnouncementNotFoundException, KeyException, InvalidAnnouncementException, InvalidWtsException {
        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(1, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id, client1Keys.getPublic()), null, 1);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client1Keys.getPrivate());
        String out = server.postGeneral(client1Keys.getPublic(), ann, wts+1);
        String id1 = out.replace("Announcement successfully posted with id ", "").replace(" to personal board.", "");

        Packet p = new Packet();
        Announcement a1 = server.getAnnouncementById(id1.toCharArray(), p);
        Assert.assertEquals(p.getWts(), 1);



        int wts2 = server.getChannelWts(1, client1Keys.getPublic());
        Assert.assertEquals(wts2, 1);

        Announcement ann2 = new Announcement("ANN02".toCharArray(), new User(id, client1Keys.getPublic()), null, 1);
        ann2.setWts(wts2+1);
        MessageSigner.sign(ann2, client1Keys.getPrivate());
        Announcement ann3 = new Announcement("ANN02".toCharArray(), new User(id, client1Keys.getPublic()), new Announcement[] {a1}, 1);
        ann3.setSignature(ann2.getSignature());
        server.postGeneral(client1Keys.getPublic(), ann3, wts2+1);
    }
*/

    @Test
    public void duplicateAnnouncement() throws UserNotFoundException, KeyException, InvalidAnnouncementException, InvalidWtsException {
        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(1, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id, client1Keys.getPublic()), null, 1);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client1Keys.getPrivate());
        server.postGeneral(client1Keys.getPublic(), ann, wts+1);
        String out = server.postGeneral(client1Keys.getPublic(), ann, wts+1);

        Assert.assertEquals("Duplicate Announcement", out);
    }

    /*@Test (expectedExceptions = InvalidAnnouncementException.class)
    public void wtsMismatch() throws InvalidWtsException, UserNotFoundException, KeyException, InvalidAnnouncementException {
        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts() + 1).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(1, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id, client1Keys.getPublic()), null, 1);
        ann.setWts(wts);
        MessageSigner.sign(ann, client1Keys.getPrivate());
        server.postGeneral(client1Keys.getPublic(), ann, wts + 1);
    }*/

    /*@Test (expectedExceptions = InvalidAnnouncementException.class)
    public void invalidWts() throws InvalidWtsException, UserNotFoundException, KeyException, InvalidAnnouncementException {
        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts() + 1).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(1, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id, client1Keys.getPublic()), null, 1);
        ann.setWts(wts+1);
        MessageSigner.sign(ann, client1Keys.getPrivate());
        server.postGeneral(client1Keys.getPublic(), ann, -1);
    }*/

    /*@Test
    public void successBuffer() throws InvalidWtsException, UserNotFoundException, AnnouncementNotFoundException, KeyException, InvalidAnnouncementException {

        int id = Integer.parseInt(server.register(client1Keys.getPublic(), server.getRegisterWts()+1).replace("Successfully logged in, your id is ", ""));

        int wts = server.getChannelWts(1, client1Keys.getPublic());
        Assert.assertEquals(wts, 0);

        Announcement ann = new Announcement("ANN01".toCharArray(), new User(id, client1Keys.getPublic()), null, 1);
        ann.setWts(wts+2);
        MessageSigner.sign(ann, client1Keys.getPrivate());
        String out = server.postGeneral(client1Keys.getPublic(), ann, wts+2);
        String id1 = out.replace("Announcement successfully posted with id ", "").replace(" to personal board.", "");

        Assert.assertEquals(server.getChannelWts(1, client1Keys.getPublic()), 0);



        int wts2 = server.getChannelWts(1, client1Keys.getPublic());
        Assert.assertEquals(wts2, 0);

        Announcement ann2 = new Announcement("ANN02".toCharArray(), new User(id, client1Keys.getPublic()), null, 1);
        ann2.setWts(wts2+1);
        MessageSigner.sign(ann2, client1Keys.getPrivate());
        String out2 = server.postGeneral(client1Keys.getPublic(), ann2, wts2+1);
        String id2 = out2.replace("Announcement successfully posted with id ", "").replace(" to personal board.", "");

        Packet p2 = new Packet();
        Announcement a2 = server.getAnnouncementById(id2.toCharArray(), p2);
        Assert.assertEquals(p2.getWts(), 2);

        Packet p = new Packet();
        Announcement a1 = server.getAnnouncementById(id1.toCharArray(), p);


        AssertJUnit.assertArrayEquals(a1.getMessage(), "ANN01".toCharArray());
        Assert.assertEquals(a1.getCreator().getPk(), client1Keys.getPublic());
        Assert.assertEquals(a1.getBoard(), 1);
        Assert.assertNull(a1.getRefs());
        Assert.assertEquals(a1.getWts(), wts+2);
        Assert.assertTrue(MessageSigner.verify(a1));
        Assert.assertTrue(MessageSigner.verify(a1, client1Keys.getPublic()));

        AssertJUnit.assertArrayEquals(a2.getMessage(), "ANN02".toCharArray());
        Assert.assertEquals(a2.getCreator().getPk(), client1Keys.getPublic());
        Assert.assertEquals(a2.getBoard(), 1);
        Assert.assertNull(a2.getRefs());
        Assert.assertEquals(a2.getWts(), wts2+1);
        Assert.assertTrue(MessageSigner.verify(a2));
        Assert.assertTrue(MessageSigner.verify(a2, client1Keys.getPublic()));

    }*/
}
