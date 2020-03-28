package server;

import commonClasses.exceptions.KeyException;
import commonClasses.exceptions.UserNotFoundException;
import org.junit.Assert;
import org.junit.Test;

public class RegisterTests extends ServerTestsBase{


    @Test
    public void success() throws UserNotFoundException, KeyException {
        int id1 = Integer.getInteger(server.register(client1Keys.getPublic(), "TEST01").replace("Successful, your id is ", ""));
        int id2 = Integer.getInteger(server.register(client2Keys.getPublic(), "TEST02").replace("Successful, your id is ", ""));
        int id3 = Integer.getInteger(server.register(client3Keys.getPublic(), "TEST03").replace("Successful, your id is ", ""));

        Assert.assertEquals(server.getUserById(id1).getPk(), client1Keys.getPublic());
        Assert.assertEquals(server.getUserById(id2).getPk(), client2Keys.getPublic());
        Assert.assertEquals(server.getUserById(id3).getPk(), client3Keys.getPublic());
    }

    @Test(expected = KeyException.class)
    public void nullKey() throws KeyException {
        server.register(client1Keys.getPublic(), "TEST01");
    }

    @Test
    public void duplicateUser() throws UserNotFoundException, KeyException {
        int id1 = Integer.getInteger(server.register(client1Keys.getPublic(), "TEST01").replace("Successful, your id is ", ""));
        int id2 = Integer.getInteger(server.register(client1Keys.getPublic(), "TEST01").replace("Successful, your id is ", ""));


        Assert.assertEquals(server.getUserById(id1).getPk(), client1Keys.getPublic());
        Assert.assertEquals(server.getUserById(id2).getPk(), client1Keys.getPublic());
        Assert.assertEquals(id1, id2);
    }

    @Test
    public void getUserSuccess() throws UserNotFoundException, KeyException {
        int id1 = Integer.getInteger(server.register(client1Keys.getPublic(), "TEST01").replace("Successful, your id is ", ""));

        Assert.assertEquals(server.getUserById(id1).getPk(), client1Keys.getPublic());
    }

    @Test(expected = UserNotFoundException.class)
    public void getUserNotExists() throws UserNotFoundException {
        server.getUserById(5).getPk();
    }




}
