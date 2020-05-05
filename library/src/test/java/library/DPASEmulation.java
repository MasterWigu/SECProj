package library;

/*
public class DPASEmulation implements ICommLib {
    public PublicKey tempPublicKey = null;
    public String tempUsername;
    public char[] tempMessage;
    public Announcement[] tempAs;
    public long tempTime;
    public byte[] tempSign;
    public int tempNumber;
    public int tempId;

    @Override
    public String register(PublicKey key, String username) {
        tempPublicKey = key;
        tempUsername = username;
        return "UserAddedTest";
    }

    @Override
    public String post(PublicKey key, char[] message, Announcement[] a, long time, byte[] sign) throws UserNotFoundException {
        tempPublicKey = key;
        tempMessage = message;
        tempAs = a;
        tempTime = time;
        tempSign = sign;
        if (Arrays.equals(tempMessage, "ERROR_POST".toCharArray()))
            throw new UserNotFoundException();
        return "PostedCreatedTest";
    }

    @Override
    public String postGeneral(PublicKey key, char[] message, Announcement[] a, long time, byte[] sign) throws UserNotFoundException {
        tempPublicKey = key;
        tempMessage = message;
        tempAs = a;
        tempTime = time;
        tempSign = sign;
        if (Arrays.equals(tempMessage, "ERROR_POST_GENERAL".toCharArray()))
            throw new UserNotFoundException();
        return "PostedGeneralCreatedTest";
    }

    @Override
    public Announcement[] read(PublicKey key, int number) throws UserNotFoundException {
        tempPublicKey = key;
        tempNumber = number;
        if (tempNumber < 0)
            throw new UserNotFoundException();
        return new Announcement[]{new Announcement("Read".toCharArray(), null, null, 0, 0, null)};
    }

    @Override
    public Announcement[] readGeneral(int number) {
        tempNumber = number;
        if (tempNumber == 123456)
            return new Announcement[]{new Announcement("ReadGeneral".toCharArray(), null, null, 0, 0, null)};
        return null;
    }

    @Override
    public Announcement getAnnouncementById(int id) throws AnnouncementNotFoundException {
        tempId = id;
        if (tempId == -2)
            throw new AnnouncementNotFoundException();
        return new Announcement("SuccessfulAnnouncement".toCharArray(), null, null, 0, 0, null);
    }

    @Override
    public User getUserById(int id) throws UserNotFoundException {
        tempId = id;
        if (tempId == -2)
            throw new UserNotFoundException();
        return new User(0, null, "SuccessfulUser");
    }
}*/
