package dao;


import domain.Level;
import domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.mail.MailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import service.MockMailSender;
import service.impl.UserServiceImpl;
import service.impl.UserServiceTx;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import java.sql.SQLException;

import static org.junit.Assert.*;
import static service.impl.UserServiceImpl.MIN_LOGIN_FOR_SILVER;
import static service.impl.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DaoFactory.class)
@DirtiesContext
public class UserDaoImplTest {

    @Autowired
    private ApplicationContext context;
    @Autowired
    DataSource dataSource;

    @Autowired
    UserServiceImpl service;

    @Autowired
    MailSender mailSender;

    @Autowired
    PlatformTransactionManager transactionManager;

    private UserDaoImpl dao;
    private User user;
    private User user1;
    private User user2;

    private User user3;

    private User user4;

    // fixture
    List<User> users;

    @Before
    public void setUp(){
        this.dao = context.getBean("userDao", UserDaoImpl.class);

        user = new User("1","hero","1234","user@email.com",Level.BASIC
                ,MIN_LOGIN_FOR_SILVER-1,0);
        user1 = new User("2","kim","1234","user1@emai.com",Level.BASIC
                ,MIN_LOGIN_FOR_SILVER,0);
        user2 = new User("3","lee","1234","user2@email.com",Level.SILVER
                ,60,MIN_RECOMMEND_FOR_GOLD-1);
        user3 = new User("4","name3","1234","user3@email.com",Level.SILVER
                ,60,MIN_RECOMMEND_FOR_GOLD);

        user4  = new User("5","user4","1234","user4@email.com",Level.GOLD,
                100,Integer.MAX_VALUE);
        users = Arrays.asList(user,user1,user2,user3,user4);

    }

    @Test
    public void addAll()throws SQLException{
        dao.deleteAll();
        for (User usr:users)
            dao.add(usr);

        List<User> users = dao.getAll();
        assertEquals(users.size(),5);
        assertEquals(dao.getCount(),5);
    }

    @Test
    public void addAndGet() {

        dao.deleteAll();
        assertEquals(dao.getCount(),0);

        dao.add(user);
        User userget1 = dao.get("1");
        checkSameUser(user,userget1);

        dao.add(user1);
        User userget2 = dao.get("2");
        assertEquals(dao.getCount(),2);
        checkSameUser(user1,userget2);

    }

    @Test
    public void delete() throws SQLException {
        int res = dao.deleteAll();
        assertEquals(res,0);
    }

    @Test(expected = DuplicateKeyException.class)
    public void checkException() throws SQLException {
        dao.deleteAll();
        dao.add(user);
        dao.add(user);
    }

    @Test(expected = DuplicateKeyException.class)
    public void getCause(){
        dao.deleteAll();
        dao.add(user1);
        dao.add(user1);

    }

    @Test
    public void sqlExceptionTranslate(){
        dao.deleteAll();

        try {
            dao.add(user1);
            dao.add(user1);
        }catch (DuplicateKeyException ex){
            SQLException sqlex = (SQLException) ex.getRootCause();
            SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
            System.out.println(set.translate(null,null,sqlex));
        }
    }

    @Test
    public void update(){
        dao.deleteAll();
        assertEquals(dao.getCount(),0);

        dao.add(user1);
        assertEquals(dao.getCount(),1);
        user1.setName("asdf");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        int res = dao.update(user1);
        assertEquals(res,1);

        dao.add(user2);
        assertEquals(dao.getCount(),2);
        User user2update = dao.get(user2.getId());
        checkSameUser(user2update,user2);

    }

    @Test
    public void bean(){
        assertNotNull(service);
    }

    @Test
    public void upgradeLevels() throws SQLException {
        dao.deleteAll();

        for (User user:users)
            dao.add(user);

        MockMailSender mockMailSender = new MockMailSender();
        service.setMailSender(mockMailSender);

        service.upgradeLevels();

        checkLevel(users.get(0),false);
        checkLevel(users.get(1),true);
        checkLevel(users.get(2),false);
        checkLevel(users.get(3),true);
        checkLevel(users.get(4),false);

        List<String> request = mockMailSender.getRequests();
        assertEquals(request.size(),2);
    }

    @Test
    public void add(){
        dao.deleteAll();

        User userWithLevel = users.get(4);

        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        service.add(userWithLevel);
        service.add(userWithoutLevel);

        User userWithLevelRead = dao.get(userWithLevel.getId());
        User userWithoutLevelRead = dao.get(userWithoutLevel.getId());

        assertEquals(userWithLevelRead.getLevel(),userWithLevel.getLevel());
        assertEquals(userWithoutLevelRead.getLevel(),Level.BASIC);
    }

    @Test
    public void upgradeAllorNothing(){
        UserServiceImpl testUserService = new UserServiceTest(users.get(3).getId());
        testUserService.setUserDao(this.dao);
        testUserService.setMailSender(mailSender);

        UserServiceTx tx = new UserServiceTx();
        tx.setTransactionManager(transactionManager);
        tx.setUserService(testUserService);

        dao.deleteAll();

        for (User user:users)
            dao.add(user);

        try {
            tx.upgradeLevels();
        }catch (TestUserServiceException e){

        }

        checkLevel(users.get(1),false);
    }

    private void checkLevel(User user, boolean upgraded) {
        User usr = dao.get(user.getId());
        if(upgraded)
            assertEquals(usr.getLevel(),user.getLevel().nextLevel());
        else
            assertEquals(usr.getLevel(),user.getLevel());
    }


    private void checkSameUser(User user1,User user2){
        assertEquals(user1.getId(),user2.getId());
        assertEquals(user1.getName(),user2.getName());
        assertEquals(user1.getPassword(),user2.getPassword());
        assertEquals(user1.getLevel(),user2.getLevel());
        assertEquals(user1.getLogin(),user2.getLogin());
        assertEquals(user1.getRecommend(),user2.getRecommend());
    }

    public static class UserServiceTest extends UserServiceImpl {
        private String id;

        public UserServiceTest(String id){
            this.id = id;
        }

        @Override
        protected void upgradeLevel(User user){
            if(user.getId().equals(this.id))
                super.upgradeLevel(user);
        }
    }

    static class TestUserServiceException extends RuntimeException{

    }

}
