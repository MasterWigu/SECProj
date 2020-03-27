package library;

import keyStoreCreator.KeyStoreCreator;
import org.junit.After;
import org.junit.Before;

import java.security.KeyPair;

public class SocketsTest {
    SocketClient clientSocket1;
    SocketClient clientSocket2;
    SocketClient clientSocket3;
    KeyPair serverKeys;
    KeyPair client1Keys;
    KeyPair client2Keys;
    KeyPair client3Keys;
    private SocketServer serverListener;
    SocketProcessorEmulator serverProcessor;



    @Before
    public void setUp() {
        serverKeys = KeyStoreCreator.createKeyPair();
        client1Keys = KeyStoreCreator.createKeyPair();
        client2Keys = KeyStoreCreator.createKeyPair();
        client3Keys = KeyStoreCreator.createKeyPair();


        serverListener = new SocketServer(serverProcessor, 10251, serverKeys.getPrivate());
        serverListener.createWorker();
        serverListener.start();


        clientSocket1 = new SocketClient("localhost", 10251, client1Keys.getPublic());
        clientSocket2 = new SocketClient("localhost", 10251, client2Keys.getPublic());
        clientSocket3 = new SocketClient("localhost", 10251, client3Keys.getPublic());

    }

    @After
    public void close() {
        serverListener.stop();
    }
}
