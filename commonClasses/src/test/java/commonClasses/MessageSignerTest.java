package commonClasses;

import keyStoreCreator.KeyStoreCreator;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.security.KeyPair;

public class MessageSignerTest {

    @Test
    public void success() {
        KeyPair keys = KeyStoreCreator.createKeyPair();

        Announcement a1 = new Announcement("TEST_Message".toCharArray(), new User(1, keys.getPublic()), null, 1);
        MessageSigner.sign(a1, keys.getPrivate());

        Assert.assertTrue(MessageSigner.verify(a1));
    }

    @Test
    public void successReffs() {
        KeyPair keys = KeyStoreCreator.createKeyPair();

        Announcement a1 = new Announcement("TEST_Message1".toCharArray(), new User(1,keys.getPublic()), null, 1);
        MessageSigner.sign(a1, keys.getPrivate());
        Announcement a2 = new Announcement("TEST_Message2".toCharArray(), new User(1,keys.getPublic()), new Announcement[]{a1}, 1);
        MessageSigner.sign(a2, keys.getPrivate());
        Announcement a3 = new Announcement("TEST_Message3".toCharArray(), new User(1,keys.getPublic()), new Announcement[]{a1, a2}, 1);
        MessageSigner.sign(a3, keys.getPrivate());

        Assert.assertTrue(MessageSigner.verify(a3));
    }

    @Test
    public void nullPublicKey() {
        KeyPair keys = KeyStoreCreator.createKeyPair();
        Announcement a = new Announcement("TEST_Message1".toCharArray(), new User(1,null), null, 1);
        MessageSigner.sign(a, keys.getPrivate());

        Assert.assertFalse(MessageSigner.verify(a));
    }

    @Test
    public void nullPrivateKey() {
        KeyPair keys = KeyStoreCreator.createKeyPair();
        Announcement a = new Announcement("TEST_Message1".toCharArray(), new User(1, keys.getPublic()), null, 1);
        MessageSigner.sign(a, null);
        Assert.assertNull(a.getSignature());
    }

   @Test
    public void differentKeys1() {
        KeyPair keys1 = KeyStoreCreator.createKeyPair();
        KeyPair keys2 = KeyStoreCreator.createKeyPair();
        Announcement a = new Announcement("TEST_Message1".toCharArray(), new User(1,keys1.getPublic()), null, 1);
        MessageSigner.sign(a, keys2.getPrivate());

        Assert.assertFalse(MessageSigner.verify(a));
    }

    @Test
    public void differentKeys2() {
        KeyPair keys1 = KeyStoreCreator.createKeyPair();
        KeyPair keys2 = KeyStoreCreator.createKeyPair();
        Announcement a = new Announcement("TEST_Message1".toCharArray(), new User(1,keys2.getPublic()), null, 1);
        MessageSigner.sign(a, keys1.getPrivate());

        Assert.assertFalse(MessageSigner.verify(a));
    }


    @Test(expectedExceptions = NullPointerException.class)
    public void nullMessageVerify() {
        Assert.assertFalse(MessageSigner.verify(null));
    }
}
