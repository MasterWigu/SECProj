package library;

import commonClasses.Announcement;
import commonClasses.User;
import keyStoreCreator.KeyStoreCreator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.KeyPair;

public class PacketSignerTest {


    @Test
    public void success1() {
        KeyPair keys = KeyStoreCreator.createKeyPair();

        Packet p = new Packet();
        p.setFunction(Packet.Func.REGISTER);
        p.setSenderPk(keys.getPublic());
        p.setUsername("Test");

        p = PacketSigner.sign(p, keys.getPrivate());

        Assert.assertTrue(PacketSigner.verify(p, null));
    }

    @Test
    public void success2() {
        KeyPair keys = KeyStoreCreator.createKeyPair();

        Packet p = new Packet();
        p.setFunction(Packet.Func.GET_ANN_ID);
        p.setId(0);
        p.setSenderPk(keys.getPublic());

        p = PacketSigner.sign(p, keys.getPrivate());

        Assert.assertTrue(PacketSigner.verify(p, null));
    }


    @Test
    public void success3() {
        KeyPair keys = KeyStoreCreator.createKeyPair();

        Announcement a = new Announcement("TEST".toCharArray(), new User(0, null, ""), null, 0, 0, null);

        Packet p = new Packet();
        p.setFunction(Packet.Func.POST);
        p.setSenderPk(keys.getPublic());
        p.setMessage("TEST001".toCharArray());
        p.setAnnouncements(new Announcement[]{a});
        p.setMessageSignature(null);

        p = PacketSigner.sign(p, keys.getPrivate());

        Assert.assertTrue(PacketSigner.verify(p, null));
    }

    @Test
    public void success4() {
        KeyPair keys = KeyStoreCreator.createKeyPair();

        Packet p = new Packet();
        p.setFunction(Packet.Func.REGISTER);
        p.setSenderPk(keys.getPublic());
        p.setUsername("Test");

        p = PacketSigner.sign(p, keys.getPrivate());

        Assert.assertTrue(PacketSigner.verify(p, keys.getPublic()));
    }

    @Test
    public void forcedKeyInvalid() {
        KeyPair keys1 = KeyStoreCreator.createKeyPair();
        KeyPair keys2 = KeyStoreCreator.createKeyPair();

        Packet p = new Packet();
        p.setFunction(Packet.Func.REGISTER);
        p.setSenderPk(keys1.getPublic());
        p.setUsername("Test");

        p = PacketSigner.sign(p, keys1.getPrivate());


        Assert.assertFalse(PacketSigner.verify(p, keys2.getPublic()));
    }

    @Test
    public void changed1() {
        KeyPair keys = KeyStoreCreator.createKeyPair();

        Packet p = new Packet();
        p.setFunction(Packet.Func.REGISTER);
        p.setSenderPk(keys.getPublic());
        p.setUsername("Test");

        p = PacketSigner.sign(p, keys.getPrivate());

        p.setUsername("notTest");
        Assert.assertFalse(PacketSigner.verify(p, null));
    }

    @Test
    public void changed2() {
        KeyPair keys = KeyStoreCreator.createKeyPair();

        Packet p = new Packet();
        p.setFunction(Packet.Func.GET_ANN_ID);
        p.setId(0);
        p.setSenderPk(keys.getPublic());

        p = PacketSigner.sign(p, keys.getPrivate());
        p.setId(1);

        Assert.assertFalse(PacketSigner.verify(p, null));
    }


    @Test
    public void changed3() {
        KeyPair keys = KeyStoreCreator.createKeyPair();

        Announcement a = new Announcement("TEST".toCharArray(), new User(0, null, ""), null, 0, 0, null);

        Packet p = new Packet();
        p.setFunction(Packet.Func.POST);
        p.setSenderPk(keys.getPublic());
        p.setMessage("TEST001".toCharArray());
        p.setAnnouncements(new Announcement[]{a});
        p.setMessageSignature(null);

        p = PacketSigner.sign(p, keys.getPrivate());

        p.setAnnouncements(null);

        Assert.assertFalse(PacketSigner.verify(p, null));
    }

    @Test
    public void changed4() {
        KeyPair keys = KeyStoreCreator.createKeyPair();

        Packet p = new Packet();
        p.setFunction(Packet.Func.GET_ANN_ID);
        p.setId(0);
        p.setSenderPk(keys.getPublic());

        p = PacketSigner.sign(p, keys.getPrivate());

        p.setUser(new User(0, null, ""));

        Assert.assertFalse(PacketSigner.verify(p, null));
    }


    @Test
    public void nullPublicKey() {
        KeyPair keys = KeyStoreCreator.createKeyPair();

        Packet p = new Packet();
        p.setFunction(Packet.Func.GET_ANN_ID);
        p.setId(0);
        p.setSenderPk(null);

        p = PacketSigner.sign(p, keys.getPrivate());

        p.setUser(new User(0, null, ""));

        Assert.assertFalse(PacketSigner.verify(p, null));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void nullPrivateKey() {
        KeyPair keys = KeyStoreCreator.createKeyPair();

        Packet p = new Packet();
        p.setFunction(Packet.Func.GET_ANN_ID);
        p.setId(0);
        p.setSenderPk(keys.getPublic());

        p = PacketSigner.sign(p, null);

        p.setUser(new User(0, null, ""));

        Assert.assertFalse(PacketSigner.verify(p, null));
    }

    @Test
    public void differentKeys() {
        KeyPair keys1 = KeyStoreCreator.createKeyPair();
        KeyPair keys2 = KeyStoreCreator.createKeyPair();

        Packet p = new Packet();
        p.setFunction(Packet.Func.GET_ANN_ID);
        p.setId(0);
        p.setSenderPk(keys1.getPublic());

        p = PacketSigner.sign(p, keys2.getPrivate());

        p.setUser(new User(0, null, ""));

        Assert.assertFalse(PacketSigner.verify(p, null));
    }


    @Test(expectedExceptions = NullPointerException.class)
    public void nullPacketVerify() {
        Assert.assertFalse(PacketSigner.verify(null, null));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void nullPacketSign() {
        KeyPair keys = KeyStoreCreator.createKeyPair();
        PacketSigner.sign(null, keys.getPrivate());
    }

}
