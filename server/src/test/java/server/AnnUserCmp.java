package server;

import commonClasses.Announcement;
import commonClasses.User;
import org.testng.Assert;
import org.testng.AssertJUnit;

public class AnnUserCmp {

    public void annCmp(Announcement a1, Announcement a2) {
        Assert.assertEquals(a1.getId(), a2.getId());
        AssertJUnit.assertArrayEquals(a1.getMessage(), a2.getMessage());
        AssertJUnit.assertArrayEquals(a1.getSignature(), a2.getSignature());
        if (a1.getReffs() != null && a2.getReffs() != null) {
            Assert.assertEquals(a1.getReffs().length, a2.getReffs().length);
            for (int i = 0; i < a1.getReffs().length; i++) {
                annCmp(a1.getReffs()[i], a2.getReffs()[i]);
            }
        }
        Assert.assertEquals(a1.getBoard(), a2.getBoard());
        Assert.assertEquals(a1.getTimestamp(), a2.getTimestamp());
        userCmp(a1.getCreator(), a2.getCreator());
    }

    public void userCmp(User u1, User u2) {
        Assert.assertEquals(u1.getId(), u2.getId());
        Assert.assertEquals(u1.getPk(), u2.getPk());
        Assert.assertEquals(u1.getUsername(), u2.getUsername());
    }
}
