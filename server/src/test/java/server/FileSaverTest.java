package server;

import commonClasses.Announcement;
import commonClasses.User;
import keyStoreCreator.KeyStoreCreator;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import server.DataSerializables.GeneralBoardData;
import server.DataSerializables.PersonalBoardsData;
import server.DataSerializables.UsersData;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

public class FileSaverTest {

    private Announcement ann1;
    private Announcement ann2;
    private Announcement ann3;
    private User u1;
    private User u2;
    private User u3;
    private FileSaver f;

    @BeforeSuite
    public void setUp() {
        KeyPair k1 = KeyStoreCreator.createKeyPair();
        KeyPair k2 = KeyStoreCreator.createKeyPair();
        KeyPair k3 = KeyStoreCreator.createKeyPair();

        u1 = new User(1, k1.getPublic());
        u2 = new User(2, k2.getPublic());
        u3 = new User(3, k3.getPublic());

        ann1 = new Announcement("ANN1".toCharArray(), u1, null, 0);
        ann1.setWts(1);
        ann2 = new Announcement("ANN2".toCharArray(), u2, new Announcement[]{ann1}, 0);
        ann2.setWts(2);
        ann3 = new Announcement("ANN3".toCharArray(), u3, null, 0);
        ann3.setWts(3);

        f = FileSaver.getInstance("src\\test\\resources\\", 1);
    }

    @Test
    public void writeReadUsers1() {
        ArrayList<User> us = new ArrayList<>();
        us.add(u1);
        UsersData uss = new UsersData(us,2);
        f.writeUsers(uss);

        UsersData uRead = f.readUsers();
        Assert.assertEquals(uRead.getRegisterWts(), 2);

        for (int i = 0; i<uRead.getUsers().size(); i++) {
            Assert.assertEquals(uRead.getUsers().get(i), us.get(i));
        }
    }

    @Test
    public void writeReadUsers3() {
        ArrayList<User> us = new ArrayList<>();
        us.add(u1);
        us.add(u2);
        us.add(u3);
        UsersData uss = new UsersData(us,3);
        f.writeUsers(uss);

        UsersData uRead = f.readUsers();
        Assert.assertEquals(uRead.getRegisterWts(), 3);

        for (int i = 0; i<uRead.getUsers().size(); i++) {
            Assert.assertEquals(uRead.getUsers().get(i), us.get(i));
        }
    }

    @Test
    public void writeReadGenAnns1() {
        HashMap<Integer, ArrayList<Announcement>> generalBoard = new HashMap<>();
        Integer generalWts= 1;

        generalBoard.put(1, new ArrayList<Announcement>());

        generalBoard.get(1).add(ann1);

        GeneralBoardData generalBoardData = new GeneralBoardData(generalBoard, generalWts);

        f.writeGeneralBoard(generalBoardData);

        GeneralBoardData aRead = f.readGeneralBoard();

        Assert.assertEquals(aRead.getGeneralWts(), 1);
        Assert.assertEquals(aRead.getGeneralBoard().keySet(), generalBoard.keySet());
        Assert.assertEquals(aRead.getGeneralBoard().get(1).get(0), generalBoard.get(1).get(0));
    }

    @Test
    public void writeReadGenAnns3() {
        HashMap<Integer, ArrayList<Announcement>> generalBoard = new HashMap<>();
        Integer generalWts= 3;

        generalBoard.put(1, new ArrayList<Announcement>());
        generalBoard.put(2, new ArrayList<Announcement>());

        generalBoard.get(1).add(ann1);
        generalBoard.get(2).add(ann2);
        generalBoard.get(1).add(ann3);

        GeneralBoardData generalBoardData = new GeneralBoardData(generalBoard, generalWts);

        f.writeGeneralBoard(generalBoardData);

        GeneralBoardData aRead = f.readGeneralBoard();

        Assert.assertEquals(aRead.getGeneralWts(), 3);
        Assert.assertEquals(aRead.getGeneralBoard().keySet(), generalBoard.keySet());
        Assert.assertEquals(aRead.getGeneralBoard().get(1).get(0), generalBoard.get(1).get(0));
        Assert.assertEquals(aRead.getGeneralBoard().get(2).get(0), generalBoard.get(2).get(0));
        Assert.assertEquals(aRead.getGeneralBoard().get(1).get(1), generalBoard.get(1).get(1));
    }


    @Test
    public void writeReadPersAnns1() {
        HashMap<PublicKey, ArrayList<Announcement>> personalBoards = new HashMap<>();
        HashMap<PublicKey, ArrayList<Announcement>> personalBoardBuffers = new HashMap<>();
        HashMap<PublicKey, Integer> personalWtss = new HashMap<>();

        personalBoards.put(u1.getPk(), new ArrayList<Announcement>());
        personalBoards.put(u2.getPk(), new ArrayList<Announcement>());

        personalBoardBuffers.put(u1.getPk(), new ArrayList<Announcement>());
        personalBoardBuffers.put(u2.getPk(), new ArrayList<Announcement>());



        personalWtss.put(u1.getPk(), 1);
        personalWtss.put(u2.getPk(), 2);

        personalBoards.get(u1.getPk()).add(ann1);
        personalBoards.get(u2.getPk()).add(ann2);
        personalBoards.get(u2.getPk()).add(ann3);

        personalBoardBuffers.get(u2.getPk()).add(ann1);
        personalBoardBuffers.get(u1.getPk()).add(ann2);
        personalBoardBuffers.get(u1.getPk()).add(ann3);

        PersonalBoardsData personalBoardsData = new PersonalBoardsData(personalBoards, personalBoardBuffers, personalWtss);
        f.writePersonalBoards(personalBoardsData);

        PersonalBoardsData aRead = f.readPersonalBoards();

        Assert.assertEquals((int)aRead.getPersonalWtss().get(u1.getPk()), 1);
        Assert.assertEquals((int)aRead.getPersonalWtss().get(u2.getPk()), 2);

        Assert.assertEquals(aRead.getPersonalBoards().keySet(), personalBoards.keySet());
        Assert.assertEquals(aRead.getPersonalBoards().get(u1.getPk()).get(0), personalBoards.get(u1.getPk()).get(0));
        Assert.assertEquals(aRead.getPersonalBoards().get(u2.getPk()).get(0), personalBoards.get(u2.getPk()).get(0));
        Assert.assertEquals(aRead.getPersonalBoards().get(u2.getPk()).get(1), personalBoards.get(u2.getPk()).get(1));

        Assert.assertEquals(aRead.getPersonalBoardBuffers().keySet(), personalBoardBuffers.keySet());
        Assert.assertEquals(aRead.getPersonalBoardBuffers().get(u2.getPk()).get(0), personalBoardBuffers.get(u2.getPk()).get(0));
        Assert.assertEquals(aRead.getPersonalBoardBuffers().get(u1.getPk()).get(0), personalBoardBuffers.get(u1.getPk()).get(0));
        Assert.assertEquals(aRead.getPersonalBoardBuffers().get(u1.getPk()).get(1), personalBoardBuffers.get(u1.getPk()).get(1));
    }

}
