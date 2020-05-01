package library;

import keyStoreCreator.KeyStoreCreator;
import org.testng.annotations.*;
import org.testng.Assert;
import org.testng.AssertJUnit;

import java.security.KeyPair;
import java.util.Arrays;
/*
public class SocketsTest {
    AuthPerfectP2PLinks clientSocket1;
    AuthPerfectP2PLinks clientSocket2;
    AuthPerfectP2PLinks clientSocket3;
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


    @BeforeSuite
    public void setUp() {

        serverKeys = KeyStoreCreator.createKeyPair();
        client1Keys = KeyStoreCreator.createKeyPair();
        client2Keys = KeyStoreCreator.createKeyPair();
        client3Keys = KeyStoreCreator.createKeyPair();

        serverProcessor = new SocketProcessorEmulator(serverKeys.getPublic());
        serverListener = new SocketServer(serverProcessor, 10252, serverKeys.getPrivate(), serverKeys.getPublic());
        serverListener.createWorker();


        clientSocket1 = new AuthPerfectP2PLinks("localhost", 10252, serverKeys.getPublic());
        clientSocket2 = new AuthPerfectP2PLinks("localhost", 10252, serverKeys.getPublic());
        clientSocket3 = new AuthPerfectP2PLinks("localhost", 10252, serverKeys.getPublic());
    }

    @AfterSuite
    public void close() {
        serverListener.stop();
    }


    @Test
    public void successRegister() {
        System.out.println("SUC_REG");
        Packet send = new Packet();
        send.setSenderPk(client1Keys.getPublic());


        send.setUsername("TEST");
        send.setFunction(Packet.Func.REGISTER);


        Packet receive = clientSocket1.sendFunction(send, client1Keys.getPrivate());

        Packet inServer = serverProcessor.tempPacket;
        serverProcessor.read = true;

        Assert.assertEquals(Packet.Func.REGISTER, receive.getFunction());
        Assert.assertEquals(serverKeys.getPublic(), receive.getSenderPk());
        Assert.assertEquals(client1Keys.getPublic(), inServer.getSenderPk());
        Assert.assertNotEquals(send.getTimestamp(), receive.getTimestamp());
        Assert.assertEquals(send.getTimestamp(), inServer.getTimestamp());
        AssertJUnit.assertArrayEquals(send.getSign(), inServer.getSign());
        Assert.assertTrue(packetEq(receive, send));
    }


    @Test
    public void nullPacket() {
        System.out.println("NULL_PACK");
        Packet receive = clientSocket1.sendFunction(null, client1Keys.getPrivate());
        Assert.assertTrue(serverProcessor.read);
        Assert.assertNull(receive);

        serverProcessor.read = true;
    }

    @Test
    public void differentKeys() {
        System.out.println("DIFF_KEYS");
        Packet send = new Packet();
        send.setSenderPk(client1Keys.getPublic());
        send.setUsername("TEST");
        send.setFunction(Packet.Func.REGISTER);

        Packet receive = clientSocket1.sendFunction(send, client2Keys.getPrivate());

        Assert.assertEquals(Packet.Func.ERROR, receive.getFunction());
        AssertJUnit.assertArrayEquals("Invalid Packet received".toCharArray(), receive.getMessage());
        Assert.assertTrue(serverProcessor.read);
    }
}*/
