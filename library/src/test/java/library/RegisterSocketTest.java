package library;

import org.junit.Assert;
import org.junit.Test;

import javax.crypto.BadPaddingException;

public class RegisterSocketTest extends SocketsTest {

    @Test
    public void success() {
        Packet send = new Packet();
        send.setKey(client1Keys.getPublic());
        send.setUsername("TEST");
        send.setFunction(Packet.Func.REGISTER);

        Packet receive = clientSocket1.sendFunction(send, client1Keys.getPrivate());

        Packet inServer = serverProcessor.tempPacket;
        serverProcessor.read = true;

        Assert.assertEquals(send, receive);
        Assert.assertEquals(send, inServer);
    }

/*
    @Test(expected = NullPointerException.class)
    public void nullPacket() {
        Packet receive = clientSocket1.sendFunction(null, client1Keys.getPrivate());
        serverProcessor.read = true;

    }*/
/*
    @Test(expected = BadPaddingException.class)
    public void differentKeys() {
        Packet send = new Packet();
        send.setKey(client1Keys.getPublic());
        send.setUsername("TEST");
        send.setFunction(Packet.Func.REGISTER);

        Packet receive = clientSocket1.sendFunction(send, client2Keys.getPrivate());

        Assert.assertNull(receive);
        Assert.assertTrue(serverProcessor.read);
    }*/



}
