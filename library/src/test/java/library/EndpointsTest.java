package library;

import commonClasses.Announcement;
import commonClasses.User;
import commonClasses.exceptions.AnnouncementNotFoundException;
import commonClasses.exceptions.CommunicationError;
import commonClasses.exceptions.UserNotFoundException;
import keyStoreCreator.KeyStoreCreator;
import library.Interfaces.ISocketProcessor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.security.KeyPair;

public class EndpointsTest {
    DPASEmulation serverEnd;
    ClientEndpoint clientEnd1;
    ClientEndpoint clientEnd2;
    ClientEndpoint clientEnd3;
    KeyPair serverKeys;
    KeyPair client1Keys;
    KeyPair client2Keys;
    KeyPair client3Keys;
    private SocketServer serverListener;



    @Before
    public void setUp() {
        serverKeys = KeyStoreCreator.createKeyPair();
        client1Keys = KeyStoreCreator.createKeyPair();
        client2Keys = KeyStoreCreator.createKeyPair();
        client3Keys = KeyStoreCreator.createKeyPair();

        serverEnd = new DPASEmulation();
        ISocketProcessor processor = new ServerEndpoint(serverEnd, serverKeys.getPublic());
        serverListener = new SocketServer(processor, 10250, serverKeys.getPrivate(), serverKeys.getPublic());
        serverListener.createWorker();


        clientEnd1 = new ClientEndpoint("localhost", 10250, client1Keys.getPrivate(), client1Keys.getPublic());
        clientEnd2 = new ClientEndpoint("localhost", 10250, client2Keys.getPrivate(), client2Keys.getPublic());
        clientEnd3 = new ClientEndpoint("localhost", 10250, client3Keys.getPrivate(), client3Keys.getPublic());

    }

    @After
    public void close() {
        serverListener.stop();
    }


    // REGISTER
    @Test
    public void successRegister() {
        String response = clientEnd1.register(client1Keys.getPublic(), "NOTNOT");
        Assert.assertEquals("UserAddedTest", response);
    }

    // POST
    @Test
    public void successPost() throws UserNotFoundException {
        char[] response = clientEnd1.post(client1Keys.getPublic(), "SUCCESS_POST".toCharArray(), null, null);
        Assert.assertArrayEquals("PostedCreatedTest".toCharArray(), response);
    }

    @Test (expected = UserNotFoundException.class)
    public void userNotFound() throws UserNotFoundException {
        clientEnd1.post(client1Keys.getPublic(), "ERROR_POST".toCharArray(), null, null);
    }


    // POST GENERAL
    @Test
    public void successPostGeneral() throws UserNotFoundException {
        char[] response = clientEnd1.postGeneral(client1Keys.getPublic(), "SUCCESS_POST_GENERAL".toCharArray(), null, null);
        Assert.assertArrayEquals("PostedGeneralCreatedTest".toCharArray(), response);
    }

    @Test (expected = UserNotFoundException.class)
    public void userNotFound1() throws UserNotFoundException {
        clientEnd1.postGeneral(client1Keys.getPublic(), "ERROR_POST_GENERAL".toCharArray(), null, null);
    }

    // READ
    @Test
    public void successRead() throws UserNotFoundException {
        Announcement[] response = clientEnd1.read(client1Keys.getPublic(), 123456);

        Assert.assertArrayEquals("Read".toCharArray(), response[0].getMessage());
    }


    @Test (expected = UserNotFoundException.class)
    public void userNotFound2() throws UserNotFoundException {
        clientEnd1.read(client1Keys.getPublic(), -2);
    }

    // READ_GENERAL
    @Test
    public void successReadGeneral() {
        Announcement[] response = clientEnd1.readGeneral(client1Keys.getPublic(), 123456);

        Assert.assertArrayEquals("ReadGeneral".toCharArray(), response[0].getMessage());
    }

    // ANN_ID
    @Test
    public void successGetAnnId() throws AnnouncementNotFoundException {
        Announcement response = clientEnd1.getAnnouncementById(client1Keys.getPublic(), 123456);

        Assert.assertArrayEquals("SuccessfulAnnouncement".toCharArray(), response.getMessage());
    }


    @Test (expected = AnnouncementNotFoundException.class)
    public void userNotFound3() throws AnnouncementNotFoundException {
        clientEnd1.getAnnouncementById(client1Keys.getPublic(), -2);
    }

    // User_ID
    @Test
    public void successGetUser() throws UserNotFoundException, CommunicationError {
        User response = clientEnd1.getUserById(client1Keys.getPublic(), 123456);

        Assert.assertEquals("SuccessfulUser", response.getUsername());
    }


    @Test (expected = UserNotFoundException.class)
    public void userNotFound4() throws UserNotFoundException, CommunicationError  {
        clientEnd1.getUserById(client1Keys.getPublic(), -2);
    }
}
