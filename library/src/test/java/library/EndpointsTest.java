package library;

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


    @Test (expected = UserNotFoundException.class)
    public void successRegister() {
        String response = clientEnd1.register(client1Keys.getPublic(), "NOTNOT");

        Assert.assertArrayEquals("UserAddedTest".toCharArray(), response.toCharArray());
    }


    @Test (expected = UserNotFoundException.class)
    public void userNotFound1() {
        clientEnd1.register(client1Keys.getPublic(), "NOTNOT");

    }
}
