package dao;

import domain.Level;
import domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TobyConfigure.class)
public class UserTest {
    User user;

    @Before
    public void setUp(){
        user = new User();
    }

    @Test
    public void upgradeLevel(){
        Level[] levels = Level.values();
        for (Level level : levels){
            if(level.nextLevel() == null)
                continue;

            user.setLevel(level);
            user.upgradeLevel();
            assertEquals(user.getLevel(),level.nextLevel());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void cannotUpgradeLevels(){
        Level[] levels = Level.values();

        for (Level level : levels){
            if(level.nextLevel() != null)
                continue;

            user.setLevel(level);
            user.upgradeLevel();
        }
    }
}
