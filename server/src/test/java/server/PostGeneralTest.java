package server;

import commonClasses.Announcement;
import commonClasses.MessageSigner;
import commonClasses.exceptions.AnnouncementNotFoundException;
import commonClasses.exceptions.KeyException;
import commonClasses.exceptions.UserNotFoundException;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;


public class PostGeneralTest extends ServerTestsBase {



    @Test
    public void success() throws UserNotFoundException, AnnouncementNotFoundException, KeyException {

        server.register(client1Keys.getPublic(), "TESTU01");
        long time = System.currentTimeMillis();
        byte[] sign1 = MessageSigner.sign("ANN01".toCharArray(), client1Keys.getPublic(), 1, null, client1Keys.getPrivate());
        String out = server.postGeneral(client1Keys.getPublic(), "ANN01".toCharArray(), null, time, sign1);

        String out2 = out.replace("Announcement successfully posted with id ", "").replace(" to general board", "");
        int id = Integer.parseInt(out2);

        Announcement a = server.getAnnouncementById(id);

        AssertJUnit.assertArrayEquals(a.getMessage(), "ANN01".toCharArray());
        Assert.assertEquals(a.getCreator().getPk(), client1Keys.getPublic());
        Assert.assertEquals(a.getBoard(), 1);
        Assert.assertNull(a.getReffs());
        Assert.assertEquals(a.getTimestamp(), time);
        AssertJUnit.assertArrayEquals(sign1, a.getSignature());
    }



    @Test
    public void successWithReffs() throws UserNotFoundException, AnnouncementNotFoundException, KeyException {

        server.register(client1Keys.getPublic(), "TESTU01");
        long time1 = System.currentTimeMillis();
        byte[] sign1 = MessageSigner.sign("ANN01".toCharArray(), client1Keys.getPublic(), 1, null, client1Keys.getPrivate());
        String out1 = server.postGeneral(client1Keys.getPublic(), "ANN01".toCharArray(), null, time1, sign1);

        int id1 = Integer.parseInt(out1.replace("Announcement successfully posted with id ", "").replace(" to general board", ""));

        Announcement a1 = server.getAnnouncementById(id1);

        long time2 = System.currentTimeMillis();
        byte[] sign2 = MessageSigner.sign("ANN02".toCharArray(), client1Keys.getPublic(), 1, new Announcement[] {a1}, client1Keys.getPrivate());
        String out2 = server.postGeneral(client1Keys.getPublic(), "ANN02".toCharArray(), new Announcement[] {a1}, time2, sign2);

        int id2 = Integer.parseInt(out2.replace("Announcement successfully posted with id ", "").replace(" to general board", ""));
        Announcement a2 = server.getAnnouncementById(id2);

        AssertJUnit.assertArrayEquals(a1.getMessage(), "ANN01".toCharArray());
        Assert.assertEquals(a1.getCreator().getPk(), client1Keys.getPublic());
        Assert.assertEquals(a1.getBoard(), 1);
        Assert.assertNull(a1.getReffs());
        Assert.assertEquals(a1.getTimestamp(), time1);
        AssertJUnit.assertArrayEquals(sign1, a1.getSignature());

        AssertJUnit.assertArrayEquals(a2.getMessage(), "ANN02".toCharArray());
        Assert.assertEquals(a2.getCreator().getPk(), client1Keys.getPublic());
        Assert.assertEquals(a2.getBoard(), 1);
        Assert.assertNotNull(a2.getReffs());
        AssertJUnit.assertArrayEquals(a2.getReffs(), new Announcement[]{a1});
        Assert.assertEquals(a2.getTimestamp(), time2);
        AssertJUnit.assertArrayEquals(sign2, a2.getSignature());
    }

    @Test(expectedExceptions = UserNotFoundException.class)
    public void userNotRegistered() throws UserNotFoundException {
        long time = System.currentTimeMillis();
        byte[] sign1 = MessageSigner.sign("ANN01".toCharArray(), client1Keys.getPublic(), 1, null, client1Keys.getPrivate());
        server.postGeneral(client1Keys.getPublic(), "ANN01".toCharArray(), null, time, sign1);

    }


    @Test(expectedExceptions = UserNotFoundException.class) //TODO change to dedicated exception
    public void invalidSignatureMess() throws UserNotFoundException, KeyException {

        server.register(client1Keys.getPublic(), "TESTU01");
        long time = System.currentTimeMillis();
        byte[] sign1 = MessageSigner.sign("ANN01".toCharArray(), client1Keys.getPublic(), 1, null, client1Keys.getPrivate());
        server.postGeneral(client1Keys.getPublic(), "ANN02".toCharArray(), null, time, sign1);

    }


    @Test(expectedExceptions = UserNotFoundException.class) //TODO change to dedicated exception
    public void invalidSignaturePuk1() throws UserNotFoundException, KeyException {

        server.register(client1Keys.getPublic(), "TESTU01");
        long time = System.currentTimeMillis();
        byte[] sign1 = MessageSigner.sign("ANN01".toCharArray(), client1Keys.getPublic(), 1, null, client1Keys.getPrivate());
        server.postGeneral(client2Keys.getPublic(), "ANN01".toCharArray(), null, time, sign1);
    }


    @Test(expectedExceptions = UserNotFoundException.class) //TODO change to dedicated exception
    public void invalidSignaturePuk2() throws UserNotFoundException, KeyException {

        server.register(client1Keys.getPublic(), "TESTU01");
        long time = System.currentTimeMillis();
        byte[] sign1 = MessageSigner.sign("ANN01".toCharArray(), client2Keys.getPublic(), 1, null, client1Keys.getPrivate());
        server.postGeneral(client1Keys.getPublic(), "ANN01".toCharArray(), null, time, sign1);
    }


    @Test(expectedExceptions = UserNotFoundException.class) //TODO change to dedicated exception
    public void invalidSignatureBoard() throws UserNotFoundException, KeyException {

        server.register(client1Keys.getPublic(), "TESTU01");
        long time = System.currentTimeMillis();
        byte[] sign1 = MessageSigner.sign("ANN01".toCharArray(), client1Keys.getPublic(), 0, null, client1Keys.getPrivate());
        server.postGeneral(client1Keys.getPublic(), "ANN01".toCharArray(), null, time, sign1);
    }


    @Test(expectedExceptions = UserNotFoundException.class) //TODO change to dedicated exception
    public void invalidSignatureReffs() throws UserNotFoundException, AnnouncementNotFoundException, KeyException {
        server.register(client1Keys.getPublic(), "TESTU01");
        long time1 = System.currentTimeMillis();
        byte[] sign1 = MessageSigner.sign("ANN01".toCharArray(), client1Keys.getPublic(), 1, null, client1Keys.getPrivate());
        String out1 = server.postGeneral(client1Keys.getPublic(), "ANN01".toCharArray(), null, time1, sign1);

        int id1 = Integer.parseInt(out1.replace("Announcement successfully posted with id ", "").replace(" to general board", ""));

        Announcement a1 = server.getAnnouncementById(id1);


        long time2 = System.currentTimeMillis();
        byte[] sign2 = MessageSigner.sign("ANN02".toCharArray(), client1Keys.getPublic(), 1, null, client1Keys.getPrivate());

        server.postGeneral(client1Keys.getPublic(), "ANN02".toCharArray(), new Announcement[] {a1}, time2, sign2);
    }


    @Test
    public void duplicateAnnouncement() throws UserNotFoundException, KeyException {
        server.register(client1Keys.getPublic(), "TESTU01");
        long time1 = System.currentTimeMillis();
        byte[] sign1 = MessageSigner.sign("ANN01".toCharArray(), client1Keys.getPublic(), 1, null, client1Keys.getPrivate());


        server.postGeneral(client1Keys.getPublic(), "ANN01".toCharArray(), null, time1, sign1);
        String out1 = server.postGeneral(client1Keys.getPublic(), "ANN01".toCharArray(), null, time1, sign1);

        Assert.assertEquals("Duplicate Announcement", out1);
    }



}
