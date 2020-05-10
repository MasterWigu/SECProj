package library;

import commonClasses.SRData;
import keyStoreCreator.KeyStoreCreator;
import library.Exceptions.CommunicationErrorException;
import library.Exceptions.PacketValidationException;
import org.bouncycastle.util.Pack;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.security.KeyPair;
import java.util.Arrays;

public class SocketsTest {
    AuthPerfectP2PLinks app2p;
    SRData server;
    SRData client1;
    SRData client2;
    SocketServer serverListener;
    SocketProcessorEmulator serverProcessor;

    private boolean packetEq(Packet p1, Packet p2) {
        if (p2.getFunction() != p1.getFunction())
            return false;
        if (p2.getAuxFunction() != p1.getAuxFunction())
            return false;
        if (p2.getUser() != p1.getUser())
            return false;
        if (!Arrays.equals(p2.getMessage(), p1.getMessage()))
            return false;
        if (p2.getId() != p1.getId())
            return false;
        if (p2.getWts() != p1.getWts())
            return false;
        if (p2.getRid() != p1.getRid())
            return false;
        if (p2.getSingleAnnouncement() != null && p1.getSingleAnnouncement() != null && !p1.getSingleAnnouncement().equals(p2.getSingleAnnouncement()))
            return false;
        return Arrays.equals(p2.getCharId(), p1.getCharId());

    }


    @BeforeSuite
    public void setUp() {

        server = new SRData();
        server.setPort(10240);
        server.setHost("localhost");
        KeyPair serverKP = KeyStoreCreator.createKeyPair();
        server.setPubKey(serverKP.getPublic());
        server.setPrvKey(serverKP.getPrivate());

        client1 = new SRData();
        KeyPair client1Keys = KeyStoreCreator.createKeyPair();
        client1.setPubKey(client1Keys.getPublic());
        client1.setPrvKey(client1Keys.getPrivate());

        client2 = new SRData();
        KeyPair client2Keys = KeyStoreCreator.createKeyPair();
        client2.setPubKey(client2Keys.getPublic());
        client2.setPrvKey(client2Keys.getPrivate());


        serverProcessor = new SocketProcessorEmulator(server);
        serverListener = new SocketServer(serverProcessor, 10240, server.getPrvKey(), server.getPubKey());
        serverListener.createWorker();


        app2p = new AuthPerfectP2PLinks();
        app2p.init();

    }

    @AfterSuite
    public void close() {
        serverListener.stop();
    }


    @Test
    public void successRegister() throws PacketValidationException, CommunicationErrorException {
        System.out.println("SUC_REG");
        Packet send = new Packet();
        send.setSenderPk(client1.getPubKey());
        send.setReceiverPk(server.getPubKey());

        send.setFunction(Packet.Func.REGISTER);


        Packet receive = app2p.sendFunction(send, client1, server);

        Packet inServer = serverProcessor.tempPacket;
        serverProcessor.read = true;

        Assert.assertEquals(Packet.Func.REGISTER, receive.getFunction());
        Assert.assertEquals(server.getPubKey(), receive.getSenderPk());
        Assert.assertEquals(client1.getPubKey(), inServer.getSenderPk());
        Assert.assertNotEquals(send.getNonce(), receive.getNonce());
        Assert.assertEquals(send.getNonce(), inServer.getNonce());
        AssertJUnit.assertArrayEquals(send.getSign(), inServer.getSign());
        Assert.assertTrue(packetEq(receive, send));
    }


    @Test
    public void nullPacket() throws PacketValidationException, CommunicationErrorException{
        System.out.println("NULL_PACK");
        Packet receive = app2p.sendFunction(null, client1, server);
        Assert.assertTrue(serverProcessor.read);
        Assert.assertNull(receive);

        serverProcessor.read = true;
    }
/*
    @Test(expectedExceptions = PacketValidationException.class)
    public void invalidResponseSign() throws CommunicationErrorException, PacketValidationException {
        System.out.println("WRONG_SIGN");
        Packet send = new Packet();
        send.setSenderPk(client1.getPubKey());
        send.setFunction(Packet.Func.REGISTER);
        send.setAuxFunction(Packet.Func.ERROR);

        app2p.sendFunction(send, client1, server);
    }*/
/*
    @Test(expectedExceptions = PacketValidationException.class)
    public void wrongResponseSenderKey() throws CommunicationErrorException, PacketValidationException {
        System.out.println("DIFF_KEYS1");
        Packet send = new Packet();
        send.setSenderPk(client1.getPubKey());
        send.setAuxFunction(Packet.Func.ANN_NOT_FOUND); //just to trigger error

        app2p.sendFunction(send, client1, server);
    }

    @Test(expectedExceptions = PacketValidationException.class)
    public void wrongResponseReceiverKey() throws CommunicationErrorException, PacketValidationException {
        System.out.println("DIFF_KEYS2");
        Packet send = new Packet();
        send.setSenderPk(client1.getPubKey());
        send.setAuxFunction(Packet.Func.USER_NOT_FOUND); //just to trigger error

        app2p.sendFunction(send, client1, server);
    }*/
}
