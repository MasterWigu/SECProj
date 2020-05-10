package library;

import commonClasses.Announcement;
import commonClasses.SRData;
import commonClasses.User;
import commonClasses.exceptions.*;
import keyStoreCreator.KeyStoreCreator;
import library.Exceptions.CommunicationErrorException;
import library.Interfaces.ISocketProcessor;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class EndpointsTest {
    KeyPair client1Keys;
    KeyPair client2Keys;
    KeyPair client3Keys;
    KeyPair serverKeys1;
    KeyPair serverKeys2;
    KeyPair serverKeys3;
    KeyPair serverKeys4;
    DPASEmulation serverEnd1;
    DPASEmulation serverEnd2;
    DPASEmulation serverEnd3;
    DPASEmulation serverEnd4;
    ClientEndpoint clientEnd1;
    ClientEndpoint clientEnd2;
    ClientEndpoint clientEnd3;
    private SocketServer serverListener1;
    private SocketServer serverListener2;
    private SocketServer serverListener3;
    private SocketServer serverListener4;

    @BeforeSuite
    public void setUp() {
        serverKeys1 = KeyStoreCreator.createKeyPair();
        serverKeys2 = KeyStoreCreator.createKeyPair();
        serverKeys3 = KeyStoreCreator.createKeyPair();
        serverKeys4 = KeyStoreCreator.createKeyPair();
        client1Keys = KeyStoreCreator.createKeyPair();
        client2Keys = KeyStoreCreator.createKeyPair();
        client3Keys = KeyStoreCreator.createKeyPair();

        ArrayList<SRData> servers = new ArrayList<>();
        SRData client1 = new SRData();
        client1.setPubKey(client1Keys.getPublic());
        client1.setPrvKey(client1Keys.getPrivate());

        SRData client2 = new SRData();
        client2.setPubKey(client2Keys.getPublic());
        client2.setPrvKey(client2Keys.getPrivate());

        SRData client3 = new SRData();
        client3.setPubKey(client3Keys.getPublic());
        client3.setPrvKey(client3Keys.getPrivate());



        SRData server1 = new SRData();
        server1.setId(1);
        server1.setHost("localhost");
        server1.setPort(10250);
        server1.setPrvKey(serverKeys1.getPrivate());
        server1.setPubKey(serverKeys1.getPublic());
        serverEnd1 = new DPASEmulation(server1, client1);
        ISocketProcessor processor1 = new ServerEndpoint(serverEnd1);
        serverListener1 = new SocketServer(processor1, server1.getPort(), server1.getPrvKey(), server1.getPubKey());
        serverListener1.createWorker();
        servers.add(server1);


        SRData server2 = new SRData();
        server2.setId(2);
        server2.setHost("localhost");
        server2.setPort(10251);
        server2.setPrvKey(serverKeys2.getPrivate());
        server2.setPubKey(serverKeys2.getPublic());
        serverEnd2 = new DPASEmulation(server2, client1);
        ISocketProcessor processor2 = new ServerEndpoint(serverEnd2);
        serverListener2 = new SocketServer(processor2, server2.getPort(), server2.getPrvKey(), server2.getPubKey());
        serverListener2.createWorker();
        servers.add(server2);


        SRData server3 = new SRData();
        server3.setId(3);
        server3.setHost("localhost");
        server3.setPort(10252);
        server3.setPrvKey(serverKeys3.getPrivate());
        server3.setPubKey(serverKeys3.getPublic());
        serverEnd3 = new DPASEmulation(server3, client1);
        ISocketProcessor processor3 = new ServerEndpoint(serverEnd3);
        serverListener3 = new SocketServer(processor3, server3.getPort(), server3.getPrvKey(), server3.getPubKey());
        serverListener3.createWorker();
        servers.add(server3);


        SRData server4 = new SRData();
        server4.setId(4);
        server4.setHost("localhost");
        server4.setPort(10253);
        server4.setPrvKey(serverKeys4.getPrivate());
        server4.setPubKey(serverKeys4.getPublic());
        serverEnd4 = new DPASEmulation(server4,client1);
        ISocketProcessor processor4 = new ServerEndpoint(serverEnd4);
        serverListener4 = new SocketServer(processor4, server4.getPort(), server4.getPrvKey(), server4.getPubKey());
        serverListener4.createWorker();
        servers.add(server4);

        clientEnd1 = new ClientEndpoint(client1Keys.getPrivate(), client1Keys.getPublic(), servers, 1);
        clientEnd2 = new ClientEndpoint(client2Keys.getPrivate(), client1Keys.getPublic(), servers, 1);
        clientEnd3 = new ClientEndpoint(client3Keys.getPrivate(), client1Keys.getPublic(), servers, 1);


    }

    @AfterSuite
    public void close() {
        serverListener1.stop();
        serverListener2.stop();
        serverListener3.stop();
        serverListener4.stop();
    }


    // REGISTER
    @Test
    public void successRegister() throws KeyException, InvalidWtsException {
        String response = clientEnd1.register();
        Assert.assertEquals("UserAddedTest", response);
    }

    // POST
    @Test
    public void successPost()  throws UserNotFoundException, InvalidAnnouncementException, CommunicationErrorException  {
        char[] response = clientEnd1.post("SUCCESS_POST".toCharArray(), null);
        AssertJUnit.assertArrayEquals("PostedCreatedTest".toCharArray(), response);
    }

    @Test
    public void post1ErrorUser()  throws UserNotFoundException, InvalidAnnouncementException, CommunicationErrorException {
        char[] response = clientEnd1.post("ERROR_U1".toCharArray(), null);
        AssertJUnit.assertArrayEquals("PostedCreatedTest".toCharArray(), response);
    }

    @Test (expectedExceptions = CommunicationErrorException.class)
    public void post2ErrorUser()  throws UserNotFoundException, InvalidAnnouncementException, CommunicationErrorException {
        clientEnd1.post("ERROR_U2".toCharArray(), null);
    }

    @Test
    public void post1ErrorAnn()  throws UserNotFoundException, InvalidAnnouncementException, CommunicationErrorException {
        char[] response = clientEnd1.post("ERROR_A1".toCharArray(), null);
        AssertJUnit.assertArrayEquals("PostedCreatedTest".toCharArray(), response);
    }

    @Test (expectedExceptions = CommunicationErrorException.class)
    public void post2ErrorAnn()  throws UserNotFoundException, InvalidAnnouncementException, CommunicationErrorException {
        clientEnd1.post("ERROR_A2".toCharArray(), null);
    }


    // POST GENERAL
    @Test
    public void successPostGeneral() throws UserNotFoundException, InvalidAnnouncementException, CommunicationErrorException {
        char[] response = clientEnd1.postGeneral("SUCCESS_POST".toCharArray(), null);
        AssertJUnit.assertArrayEquals("PostedGeneralCreatedTest".toCharArray(), response);
    }

    @Test
    public void postGeneral1ErrorUser()  throws UserNotFoundException, InvalidAnnouncementException, CommunicationErrorException {
        char[] response = clientEnd1.postGeneral("ERROR_U1".toCharArray(), null);
        AssertJUnit.assertArrayEquals("PostedGeneralCreatedTest".toCharArray(), response);
    }

    @Test (expectedExceptions = CommunicationErrorException.class)
    public void postGeneral2ErrorUser()  throws UserNotFoundException, InvalidAnnouncementException, CommunicationErrorException {
        clientEnd1.postGeneral("ERROR_U2".toCharArray(), null);
    }

    @Test
    public void postGeneral1ErrorAnn()  throws UserNotFoundException, InvalidAnnouncementException, CommunicationErrorException {
        char[] response = clientEnd1.postGeneral("ERROR_A1".toCharArray(), null);
        AssertJUnit.assertArrayEquals("PostedGeneralCreatedTest".toCharArray(), response);
    }

    @Test (expectedExceptions = CommunicationErrorException.class)
    public void postGeneral2ErrorAnn()  throws UserNotFoundException, InvalidAnnouncementException, CommunicationErrorException {
        clientEnd1.postGeneral("ERROR_A2".toCharArray(), null);
    }


    // READ
    @Test
    public void successRead() throws UserNotFoundException, CommunicationErrorException {
        User u = new User(123, client1Keys.getPublic());
        Announcement[] response = clientEnd1.read(u, 2);
        AssertJUnit.assertArrayEquals("READ".toCharArray(), response[0].getMessage());
    }


    /*@Test (expectedExceptions = UserNotFoundException.class)
    public void userNotFound2() throws UserNotFoundException, CommunicationErrorException {
        clientEnd1.read(client1Keys.getPublic(), -2);
    }*/

    // READ_GENERAL
    /*@Test
    public void successReadGeneral() throws CommunicationErrorException {
        Announcement[] response = clientEnd1.readGeneral(123456);

        AssertJUnit.assertArrayEquals("ReadGeneral".toCharArray(), response[0].getMessage());
    }*/

    // ANN_ID
    /*@Test
    public void successGetAnnId() throws AnnouncementNotFoundException, CommunicationErrorException {
        Announcement response = clientEnd1.getAnnouncementById(String.valueOf(123456).toCharArray());

        AssertJUnit.assertArrayEquals("SuccessfulAnnouncement".toCharArray(), response.getMessage());
    }*/


    /*@Test (expectedExceptions = AnnouncementNotFoundException.class)
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
    }*/
}
