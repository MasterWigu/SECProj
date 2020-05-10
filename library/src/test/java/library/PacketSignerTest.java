package library;

import commonClasses.Announcement;
import commonClasses.User;
import keyStoreCreator.KeyStoreCreator;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.Test; // TODO check import

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;

public class PacketSignerTest {

    @Test
    public void success1() {
        KeyPair keys = KeyStoreCreator.createKeyPair();
        KeyPair keysR = KeyStoreCreator.createKeyPair();

        Packet p = new Packet();
        p.setFunction(Packet.Func.REGISTER);
        p.setSenderPk(keys.getPublic());
        p.setReceiverPk(keysR.getPublic());
        p.setMessage("Test".toCharArray());

        p = PacketSigner.sign(p, keys.getPrivate());

        Assert.assertTrue(PacketSigner.verify(p, null));
        Assert.assertTrue(PacketSigner.verify(p, keys.getPublic()));
    }

    @Test
    public void success2() {
        KeyPair keys = KeyStoreCreator.createKeyPair();
        KeyPair keysR = KeyStoreCreator.createKeyPair();

        Packet p = new Packet();
        p.setFunction(Packet.Func.GET_ANN_ID);
        p.setId(0);
        p.setSenderPk(keys.getPublic());
        p.setReceiverPk(keysR.getPublic());

        p = PacketSigner.sign(p, keys.getPrivate());

        Assert.assertTrue(PacketSigner.verify(p, null));
        Assert.assertTrue(PacketSigner.verify(p, keys.getPublic()));
    }


    @Test
    public void success3() {
        KeyPair keys = KeyStoreCreator.createKeyPair();
        KeyPair keysR = KeyStoreCreator.createKeyPair();

        Announcement a = new Announcement("TEST".toCharArray(), new User(0, null), null, 0, 0, null);
        HashMap<Integer, ArrayList<Announcement>> tempMap = new HashMap<>();
        ArrayList<Announcement> tempA = new ArrayList<>();
        tempA.add(a);
        tempMap.put(0, tempA);

        Packet p = new Packet();
        p.setFunction(Packet.Func.POST);
        p.setSenderPk(keys.getPublic());
        p.setReceiverPk(keysR.getPublic());
        p.setMessage("TEST001".toCharArray());
        p.setAnnouncements(tempMap); // TODO careful
        p.setSign(null);

        p = PacketSigner.sign(p, keys.getPrivate());

        Assert.assertTrue(PacketSigner.verify(p, null));
        Assert.assertTrue(PacketSigner.verify(p, keys.getPublic()));
    }

    @Test
    public void success4() {
        KeyPair keys = KeyStoreCreator.createKeyPair();
        KeyPair keysR = KeyStoreCreator.createKeyPair();

        Packet p = new Packet();
        p.setFunction(Packet.Func.REGISTER);
        p.setSenderPk(keys.getPublic());
        p.setReceiverPk(keysR.getPublic());
        p.setMessage("Test".toCharArray());

        p = PacketSigner.sign(p, keys.getPrivate());

        Assert.assertTrue(PacketSigner.verify(p, null));
        Assert.assertTrue(PacketSigner.verify(p, keys.getPublic()));
    }

    @Test
    public void forcedKeyInvalid() {
        KeyPair keys1 = KeyStoreCreator.createKeyPair();
        KeyPair keys2 = KeyStoreCreator.createKeyPair();
        KeyPair keysR = KeyStoreCreator.createKeyPair();

        Packet p = new Packet();
        p.setFunction(Packet.Func.REGISTER);
        p.setSenderPk(keys1.getPublic());
        p.setReceiverPk(keysR.getPublic());
        p.setMessage("Test".toCharArray());

        p = PacketSigner.sign(p, keys1.getPrivate());


        Assert.assertFalse(PacketSigner.verify(p, keys2.getPublic()));
    }

    @Test
    public void changed1() {
        KeyPair keys = KeyStoreCreator.createKeyPair();
        KeyPair keysR = KeyStoreCreator.createKeyPair();

        Packet p = new Packet();
        p.setFunction(Packet.Func.REGISTER);
        p.setSenderPk(keys.getPublic());
        p.setReceiverPk(keysR.getPublic());
        p.setMessage("Test".toCharArray());

        p = PacketSigner.sign(p, keys.getPrivate());

        p.setMessage("notTest".toCharArray());
        Assert.assertFalse(PacketSigner.verify(p, null));
    }

    @Test
    public void changed2() {
        KeyPair keys = KeyStoreCreator.createKeyPair();
        KeyPair keysR = KeyStoreCreator.createKeyPair();


        Packet p = new Packet();
        p.setFunction(Packet.Func.GET_ANN_ID);
        p.setId(0);
        p.setSenderPk(keys.getPublic());
        p.setReceiverPk(keysR.getPublic());

        p = PacketSigner.sign(p, keys.getPrivate());
        p.setId(1);

        Assert.assertFalse(PacketSigner.verify(p, null));
    }


    @Test
    public void changed3() {
        KeyPair keys = KeyStoreCreator.createKeyPair();
        KeyPair keysR = KeyStoreCreator.createKeyPair();

        Announcement a = new Announcement("TEST".toCharArray(), new User(0, null), null, 0, 0, null);
        HashMap<Integer, ArrayList<Announcement>> tempMap = new HashMap<>();
        ArrayList<Announcement> tempA = new ArrayList<>();
        tempA.add(a);
        tempMap.put(0, tempA);

        Packet p = new Packet();
        p.setFunction(Packet.Func.POST);
        p.setSenderPk(keys.getPublic());
        p.setReceiverPk(keysR.getPublic());
        p.setMessage("TEST001".toCharArray());
        p.setAnnouncements(tempMap);
        p.setSign(null);

        p = PacketSigner.sign(p, keys.getPrivate());

        p.setAnnouncements(null);

        Assert.assertFalse(PacketSigner.verify(p, null));
    }

    @Test
    public void changed4() {
        KeyPair keys = KeyStoreCreator.createKeyPair();
        KeyPair keysR = KeyStoreCreator.createKeyPair();

        Packet p = new Packet();
        p.setFunction(Packet.Func.GET_ANN_ID);
        p.setId(0);
        p.setSenderPk(keys.getPublic());
        p.setReceiverPk(keysR.getPublic());

        p = PacketSigner.sign(p, keys.getPrivate());

        p.setUser(new User(0, null));

        Assert.assertFalse(PacketSigner.verify(p, null));
    }


    @Test
    public void nullPublicKey() {
        KeyPair keys = KeyStoreCreator.createKeyPair();
        KeyPair keysR = KeyStoreCreator.createKeyPair();

        Packet p = new Packet();
        p.setFunction(Packet.Func.GET_ANN_ID);
        p.setId(0);
        p.setSenderPk(null);
        p.setReceiverPk(null);

        p = PacketSigner.sign(p, keys.getPrivate());

        p.setUser(new User(0, null));

        Assert.assertFalse(PacketSigner.verify(p, null));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void nullPrivateKey() {
        KeyPair keys = KeyStoreCreator.createKeyPair();
        KeyPair keysR = KeyStoreCreator.createKeyPair();

        Packet p = new Packet();
        p.setFunction(Packet.Func.GET_ANN_ID);
        p.setId(0);
        p.setSenderPk(keys.getPublic());
        p.setReceiverPk(keys.getPublic());

        p = PacketSigner.sign(p, null);

        p.setUser(new User(0, null));

        Assert.assertFalse(PacketSigner.verify(p, null));
    }

    @Test
    public void differentKeys() {
        KeyPair keys1 = KeyStoreCreator.createKeyPair();
        KeyPair keys2 = KeyStoreCreator.createKeyPair();
        KeyPair keysR = KeyStoreCreator.createKeyPair();


        Packet p = new Packet();
        p.setFunction(Packet.Func.GET_ANN_ID);
        p.setId(0);
        p.setSenderPk(keys1.getPublic());
        p.setReceiverPk(keysR.getPublic());


        p = PacketSigner.sign(p, keys2.getPrivate());

        p.setUser(new User(0, null));

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
