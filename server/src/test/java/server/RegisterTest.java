package server;

import commonClasses.exceptions.InvalidWtsException;
import commonClasses.exceptions.KeyException;
import commonClasses.exceptions.UserNotFoundException;
import library.Packet;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RegisterTest extends ServerTestsBase{




    @Test
    public void success() throws UserNotFoundException, KeyException, InvalidWtsException {
        int wts1 = server.getRegisterWts();
        Assert.assertEquals(wts1, 0);
        Packet userPack1 = new Packet();
        int id1 = Integer.parseInt(server.register(client1Keys.getPublic(), wts1+1, userPack1).replace("Successfully logged in, your id is ", ""));
        int wts2 = server.getRegisterWts();
        Assert.assertEquals(wts2, wts1+1);
        Packet userPack2 = new Packet();
        int id2 = Integer.parseInt(server.register(client2Keys.getPublic(), wts2+1, userPack2).replace("Successfully logged in, your id is ", ""));
        int wts3 = server.getRegisterWts();
        Assert.assertEquals(wts3, wts2+1);
        Packet userPack3 = new Packet();
        int id3 = Integer.parseInt(server.register(client3Keys.getPublic(), wts3+1, userPack3).replace("Successfully logged in, your id is ", ""));
        Assert.assertEquals(server.getRegisterWts(), wts3+1);

        Packet p = new Packet();

        Assert.assertEquals(server.getUserById(id1, p).getPk(), client1Keys.getPublic());
        Assert.assertEquals(server.getUserById(id1, p).getId(), id1);
        Assert.assertEquals(userPack1.getUser().getId(), id1);
        Assert.assertEquals(userPack1.getUser().getPk(), client1Keys.getPublic());
        Assert.assertEquals(p.getWts(), wts3+1);
        Assert.assertEquals(server.getUserById(id2, p).getPk(), client2Keys.getPublic());
        Assert.assertEquals(server.getUserById(id2, p).getId(), id2);
        Assert.assertEquals(userPack2.getUser().getId(), id2);
        Assert.assertEquals(userPack2.getUser().getPk(), client2Keys.getPublic());
        Assert.assertEquals(p.getWts(), wts3+1);
        Assert.assertEquals(server.getUserById(id3, p).getPk(), client3Keys.getPublic());
        Assert.assertEquals(server.getUserById(id3, p).getId(), id3);
        Assert.assertEquals(userPack3.getUser().getId(), id3);
        Assert.assertEquals(userPack3.getUser().getPk(), client3Keys.getPublic());
        Assert.assertEquals(p.getWts(), wts3+1);
    }

    @Test(expectedExceptions = KeyException.class)
    public void nullKey() throws KeyException, InvalidWtsException {
        Packet p = new Packet();
        server.register(null, 1, p);
    }

    @Test
    public void duplicateUser() throws UserNotFoundException, KeyException, InvalidWtsException {
        int wts1 = server.getRegisterWts();
        Assert.assertEquals(wts1, 0);
        Packet userPack1 = new Packet();
        int id1 = Integer.parseInt(server.register(client1Keys.getPublic(), wts1+1, userPack1).replace("Successfully logged in, your id is ", ""));
        int wts2 = server.getRegisterWts();
        Assert.assertEquals(wts2, wts1+1);
        Packet userPack2 = new Packet();
        int id2 = Integer.parseInt(server.register(client1Keys.getPublic(), wts2+1, userPack2).replace("Successfully logged in, your id is ", ""));
        Assert.assertEquals(server.getRegisterWts(), wts2);


        Packet p = new Packet();
        Assert.assertEquals(server.getUserById(id1, p).getPk(), client1Keys.getPublic());
        Assert.assertEquals(server.getUserById(id1, p).getId(), id1);
        Assert.assertEquals(userPack1.getUser().getId(), id1);
        Assert.assertEquals(userPack1.getUser().getPk(), client1Keys.getPublic());
        Assert.assertEquals(p.getWts(), wts2);
        Assert.assertEquals(server.getUserById(id2, p).getPk(), client1Keys.getPublic());
        Assert.assertEquals(server.getUserById(id2, p).getId(), id2);
        Assert.assertEquals(userPack2.getUser().getId(), id1);
        Assert.assertEquals(userPack2.getUser().getPk(), client1Keys.getPublic());
        Assert.assertEquals(p.getWts(), wts2);
        Assert.assertEquals(id1, id2);
    }

    @Test
    public void getUserSuccess() throws UserNotFoundException, KeyException, InvalidWtsException {
        int wts1 = server.getRegisterWts();
        Assert.assertEquals(wts1, 0);
        Packet userPacket = new Packet();
        int id1 = Integer.parseInt(server.register(client1Keys.getPublic(), wts1+1, userPacket).replace("Successfully logged in, your id is ", ""));

        Packet p = new Packet();
        Assert.assertEquals(server.getUserById(id1, p).getPk(), client1Keys.getPublic());
        Assert.assertEquals(userPacket.getUser().getId(), id1);
        Assert.assertEquals(userPacket.getUser().getPk(), client1Keys.getPublic());
        Assert.assertEquals(p.getWts(), wts1+1);
    }

    @Test(expectedExceptions = UserNotFoundException.class)
    public void getUserNotExists() throws UserNotFoundException {
        Packet p = new Packet();
        server.getUserById(5, p);
    }



}
