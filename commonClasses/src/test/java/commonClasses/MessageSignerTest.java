package commonClasses;

public class MessageSignerTest {
//TODO refactor
/*
    @Test
    public void success() {
        KeyPair keys = KeyStoreCreator.createKeyPair();

        byte[] sign1 = MessageSigner.sign("TEST_Message".toCharArray(), keys.getPublic(), 1, null, keys.getPrivate());

        Announcement a = new Announcement("TEST_Message".toCharArray(), new User(1,keys.getPublic(), ""), null, 1, 0, sign1);
        Assert.assertTrue(MessageSigner.verify(a));
    }

    @Test
    public void successReffs() {
        KeyPair keys = KeyStoreCreator.createKeyPair();

        byte[] sign1 = MessageSigner.sign("TEST_Message1".toCharArray(), keys.getPublic(), 1, null, keys.getPrivate());
        Announcement a1 = new Announcement("TEST_Message1".toCharArray(), new User(1,keys.getPublic(), ""), null, 1, 0, sign1);
        byte[] sign2 = MessageSigner.sign("TEST_Message2".toCharArray(), keys.getPublic(), 1, new Announcement[]{a1}, keys.getPrivate());
        Announcement a2 = new Announcement("TEST_Message2".toCharArray(), new User(1,keys.getPublic(), ""), new Announcement[]{a1}, 1, 0, sign2);
        byte[] sign3 = MessageSigner.sign("TEST_Message3".toCharArray(), keys.getPublic(), 1, new Announcement[]{a1, a2}, keys.getPrivate());
        Announcement a3 = new Announcement("TEST_Message3".toCharArray(), new User(1,keys.getPublic(), ""), new Announcement[]{a1, a2}, 1, 0, sign3);

        Assert.assertTrue(MessageSigner.verify(a3));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void nullSign() {
        KeyPair keys = KeyStoreCreator.createKeyPair();
        Announcement a = new Announcement("TEST_Message1".toCharArray(), new User(1,keys.getPublic(), ""), null, 1, 0, null);

        Assert.assertFalse(MessageSigner.verify(a));
    }

    @Test
    public void nullPublicKey() {
        KeyPair keys = KeyStoreCreator.createKeyPair();
        byte[] sign = MessageSigner.sign("TEST_Message1".toCharArray(), null, 1, null, keys.getPrivate());
        Announcement a = new Announcement("TEST_Message1".toCharArray(), new User(1,null, ""), null, 1, 0, sign);

        Assert.assertFalse(MessageSigner.verify(a));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void nullPrivateKey() {
        KeyPair keys = KeyStoreCreator.createKeyPair();
        byte[] sign = MessageSigner.sign("TEST_Message1".toCharArray(), keys.getPublic(), 1, null, null);
        Announcement a = new Announcement("TEST_Message1".toCharArray(), new User(1,keys.getPublic(), ""), null, 1, 0, sign);

        Assert.assertFalse(MessageSigner.verify(a));
    }

    @Test
    public void differentKeys1() {
        KeyPair keys1 = KeyStoreCreator.createKeyPair();
        KeyPair keys2 = KeyStoreCreator.createKeyPair();
        byte[] sign = MessageSigner.sign("TEST_Message1".toCharArray(), keys1.getPublic(), 1, null, keys2.getPrivate());
        Announcement a = new Announcement("TEST_Message1".toCharArray(), new User(1,keys1.getPublic(), ""), null, 1, 0, sign);

        Assert.assertFalse(MessageSigner.verify(a));
    }

    @Test
    public void differentKeys2() {
        KeyPair keys1 = KeyStoreCreator.createKeyPair();
        KeyPair keys2 = KeyStoreCreator.createKeyPair();
        byte[] sign = MessageSigner.sign("TEST_Message1".toCharArray(), keys1.getPublic(), 1, null, keys1.getPrivate());
        Announcement a = new Announcement("TEST_Message1".toCharArray(), new User(1,keys2.getPublic(), ""), null, 1, 0, sign);

        Assert.assertFalse(MessageSigner.verify(a));
    }


    @Test(expectedExceptions = NullPointerException.class)
    public void nullMessageVerify() {
        Assert.assertFalse(MessageSigner.verify(null));
    }*/
}
