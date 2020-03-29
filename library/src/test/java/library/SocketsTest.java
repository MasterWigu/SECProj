package library;

import keyStoreCreator.KeyStoreCreator;
import org.testng.annotations.AfterMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.security.KeyPair;
import java.util.Arrays;

public class SocketsTest {
    SocketClient clientSocket1;
    SocketClient clientSocket2;
    SocketClient clientSocket3;
    KeyPair serverKeys;
    KeyPair client1Keys;
    KeyPair client2Keys;
    KeyPair client3Keys;
    SocketServer serverListener;
    SocketProcessorEmulator serverProcessor;

    private boolean packetEq(Packet p1, Packet p2) {
        if (p2.getFunction() != p1.getFunction())
            return false;
        if (p2.getUser() != p1.getUser())
            return false;
        if (p2.getNumberOfAnnouncements() != p1.getNumberOfAnnouncements())
            return false;
        if (!Arrays.equals(p2.getMessage(), p1.getMessage()))
            return false;
        if (p2.getId() != p1.getId())
            return false;
        return Arrays.equals(p2.getMessageSignature(), p1.getMessageSignature());

    }


    @BeforeMethod
    public void setUp() {
        serverKeys = KeyStoreCreator.createKeyPair();
        client1Keys = KeyStoreCreator.createKeyPair();
        client2Keys = KeyStoreCreator.createKeyPair();
        client3Keys = KeyStoreCreator.createKeyPair();

        serverProcessor = new SocketProcessorEmulator(serverKeys.getPublic());
        serverListener = new SocketServer(serverProcessor, 10251, serverKeys.getPrivate(), serverKeys.getPublic());
        serverListener.createWorker();


        clientSocket1 = new SocketClient("localhost", 10251, serverKeys.getPublic());
        clientSocket2 = new SocketClient("localhost", 10251, serverKeys.getPublic());
        clientSocket3 = new SocketClient("localhost", 10251, serverKeys.getPublic());

    }

    @AfterMethod
    public void close() {
        serverListener.stop();
    }


    @Test(singleThreaded = true)
    public void successRegister() {
        Packet send = new Packet();
        send.setKey(client1Keys.getPublic());


        send.setUsername("TEST");
        send.setFunction(Packet.Func.REGISTER);


        Packet receive = clientSocket1.sendFunction(send, client1Keys.getPrivate());

        Packet inServer = serverProcessor.tempPacket;
        serverProcessor.read = true;

        Assert.assertEquals(Packet.Func.REGISTER, receive.getFunction());
        Assert.assertEquals(serverKeys.getPublic(), receive.getKey());
        Assert.assertEquals(client1Keys.getPublic(), inServer.getKey());
        Assert.assertNotEquals(send.getTimestamp(), receive.getTimestamp());
        Assert.assertEquals(send.getTimestamp(), inServer.getTimestamp());
        AssertJUnit.assertArrayEquals(send.getSign(), inServer.getSign());
        Assert.assertTrue(packetEq(receive, send));
    }


    @Test
    public void nullPacket() {
        Packet receive = clientSocket1.sendFunction(null, client1Keys.getPrivate());
        Assert.assertTrue(serverProcessor.read);
        Assert.assertNull(receive);

        serverProcessor.read = true;
    }

    @Test
    public void differentKeys() {
        Packet send = new Packet();
        send.setKey(client1Keys.getPublic());
        send.setUsername("TEST");
        send.setFunction(Packet.Func.REGISTER);

        Packet receive = clientSocket1.sendFunction(send, client2Keys.getPrivate());

        Assert.assertEquals(Packet.Func.ERROR, receive.getFunction());
        AssertJUnit.assertArrayEquals("Invalid Packet received".toCharArray(), receive.getMessage());
        Assert.assertTrue(serverProcessor.read);
    }
}
