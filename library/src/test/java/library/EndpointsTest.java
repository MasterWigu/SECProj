package library;

import commonClasses.Announcement;
import commonClasses.User;
import commonClasses.exceptions.AnnouncementNotFoundException;
import library.Exceptions.CommunicationErrorException;
import commonClasses.exceptions.InvalidAnnouncementException;
import commonClasses.exceptions.UserNotFoundException;
import keyStoreCreator.KeyStoreCreator;
import library.Interfaces.ISocketProcessor;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.*;

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



    @BeforeSuite
    public void setUp() {
        System.out.println("START_END");
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

    @AfterSuite
    public void close() {
        System.out.println("STOP_END");
        serverListener.stop();
    }


    // REGISTER
    @Test
    public void successRegister() throws CommunicationErrorException {
        String response = clientEnd1.register(client1Keys.getPublic(), "NOTNOT");
        Assert.assertEquals("UserAddedTest", response);
    }

    // POST
    @Test
    public void successPost() throws UserNotFoundException, InvalidAnnouncementException, CommunicationErrorException {
        char[] response = clientEnd1.post(client1Keys.getPublic(), "SUCCESS_POST".toCharArray(), null, null);
        AssertJUnit.assertArrayEquals("PostedCreatedTest".toCharArray(), response);
    }

    @Test (expectedExceptions = UserNotFoundException.class)
    public void userNotFound() throws UserNotFoundException, InvalidAnnouncementException, CommunicationErrorException {
        clientEnd1.post(client1Keys.getPublic(), "ERROR_POST".toCharArray(), null, null);
    }


    // POST GENERAL
    @Test
    public void successPostGeneral() throws UserNotFoundException, InvalidAnnouncementException, CommunicationErrorException {
        char[] response = clientEnd1.postGeneral(client1Keys.getPublic(), "SUCCESS_POST_GENERAL".toCharArray(), null, null);
        AssertJUnit.assertArrayEquals("PostedGeneralCreatedTest".toCharArray(), response);
    }

    @Test (expectedExceptions = UserNotFoundException.class)
    public void userNotFound1() throws UserNotFoundException, InvalidAnnouncementException, CommunicationErrorException {
        clientEnd1.postGeneral(client1Keys.getPublic(), "ERROR_POST_GENERAL".toCharArray(), null, null);
    }

    // READ
    @Test
    public void successRead() throws UserNotFoundException, CommunicationErrorException {
        Announcement[] response = clientEnd1.read(client1Keys.getPublic(), 123456);

        AssertJUnit.assertArrayEquals("Read".toCharArray(), response[0].getMessage());
    }


    @Test (expectedExceptions = UserNotFoundException.class)
    public void userNotFound2() throws UserNotFoundException, CommunicationErrorException {
        clientEnd1.read(client1Keys.getPublic(), -2);
    }

    // READ_GENERAL
    @Test
    public void successReadGeneral() throws CommunicationErrorException {
        Announcement[] response = clientEnd1.readGeneral(client1Keys.getPublic(), 123456);

        AssertJUnit.assertArrayEquals("ReadGeneral".toCharArray(), response[0].getMessage());
    }

    // ANN_ID
    @Test
    public void successGetAnnId() throws AnnouncementNotFoundException, CommunicationErrorException {
        Announcement response = clientEnd1.getAnnouncementById(client1Keys.getPublic(), 123456);

        AssertJUnit.assertArrayEquals("SuccessfulAnnouncement".toCharArray(), response.getMessage());
    }


    @Test (expectedExceptions = AnnouncementNotFoundException.class)
    public void userNotFound3() throws AnnouncementNotFoundException, CommunicationErrorException {
        clientEnd1.getAnnouncementById(client1Keys.getPublic(), -2);
    }

    // User_ID
    @Test
    public void successGetUser() throws UserNotFoundException, CommunicationErrorException {
        User response = clientEnd1.getUserById(client1Keys.getPublic(), 123456);

        Assert.assertEquals("SuccessfulUser", response.getUsername());
    }


    @Test (expectedExceptions = UserNotFoundException.class)
    public void userNotFound4() throws UserNotFoundException, CommunicationErrorException {
        clientEnd1.getUserById(client1Keys.getPublic(), -2);
    }

    // CONCURRENCY TEST
    @Test (invocationCount=10, threadPoolSize=3)
    public void successConcurrentPost() throws UserNotFoundException, InvalidAnnouncementException, CommunicationErrorException {
        char[] response1 = clientEnd1.post(client1Keys.getPublic(), "SUCCESS_POST1".toCharArray(), null, null);
        char[] response2 = clientEnd2.post(client2Keys.getPublic(), "SUCCESS_POST2".toCharArray(), null, null);
        char[] response3 = clientEnd3.post(client3Keys.getPublic(), "SUCCESS_POST3".toCharArray(), null, null);
        AssertJUnit.assertArrayEquals("PostedCreatedTest".toCharArray(), response1);
        AssertJUnit.assertArrayEquals("PostedCreatedTest".toCharArray(), response2);
        AssertJUnit.assertArrayEquals("PostedCreatedTest".toCharArray(), response3);
    }
}
