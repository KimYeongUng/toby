package dao;


import domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DaoFactory.class)
public class UserDaoTest {

    @Autowired
    private ApplicationContext context;
    private UserDao dao;
    private User user;
    private User user1;
    private User user2;

    @Before
    public void setUp(){
        this.dao = context.getBean("userDao",UserDao.class);

        user = new User();
        user.setId("1");
        user.setName("hero");
        user.setPassword("1234");

        user1 = new User();
        user1.setId("2");
        user1.setName("kim");
        user1.setPassword("1234");

        user2 = new User();
        user2.setId("3");
        user2.setName("lee");
        user2.setPassword("1234");

    }

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {

        dao.deleteAll();
        assertEquals(dao.getCount(),0);
        dao.add(user);

        User user1 = dao.get("1");
        assertEquals(dao.getCount(),1);
        assertEquals(user.getId(),user1.getId());

    }
}
