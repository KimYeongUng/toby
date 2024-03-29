package dao;


import domain.Level;
import domain.User;
import handler.TransactionHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.opentest4j.TestAbortedException;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import service.MockMailSender;
import service.TxProxyFactoryBean;
import service.UserService;
import service.impl.UserServiceImpl;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import java.sql.SQLException;
import java.util.Objects;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;

import static org.junit.Assert.*;
import static service.impl.UserServiceImpl.MIN_LOGIN_FOR_SILVER;
import static service.impl.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TobyConfigure.class)
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

    @DisplayName("Mockito Framework")
    @Test
    public void upgradeLevels() {
        UserServiceImpl userService = new UserServiceImpl();

        // mock object UserDao
        UserDao mockUserDao = mock(UserDao.class);
        Mockito.when(mockUserDao.getAll()).thenReturn(this.users);

        // mock object MailSender
        MailSender mockMailSender = mock(MockMailSender.class);

        userService.setUserDao(mockUserDao);
        userService.setMailSender(mockMailSender);

        service.upgradeLevels();
        verify(mockUserDao,times(2)).update(any(User.class));
        verify(mockUserDao,times(2)).update(any(User.class));

        verify(mockUserDao).update(users.get(1));
        assertEquals(users.get(1).getLevel(),Level.SILVER);

        verify(mockUserDao).update(users.get(4));
        assertEquals(users.get(4),Level.GOLD);

        ArgumentCaptor<SimpleMailMessage> mailMessage =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mockMailSender,times(2)).send(mailMessage.capture());
        List<SimpleMailMessage> list = mailMessage.getAllValues();
        assertEquals(Objects.requireNonNull(list.get(0).getTo())[0],users.get(1).getEmail());
        assertEquals(Objects.requireNonNull(list.get(1).getTo())[0],users.get(4).getEmail());
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
    @DirtiesContext
    public void upgradeAllorNothing() throws Exception{
        UserServiceImpl testUserService = new UserServiceTestImpl(users.get(3).getId());
        testUserService.setUserDao(this.dao);
        testUserService.setMailSender(mailSender);
        ProxyFactoryBean factoryBean = context.getBean("&userService",ProxyFactoryBean.class);
        factoryBean.setTarget(testUserService);
        UserService userService = (UserService) factoryBean.getObject();

        dao.deleteAll();

        for (User user:users)
            dao.add(user);

        try {
            userService.upgradeLevels();
        }catch (TestUserServiceException e){
            throw e;
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

    public static class UserServiceTestImpl extends UserServiceImpl {
        private String id = "lee";

        public UserServiceTestImpl(String id){
            this.id = id;
        }

        @Override
        protected void upgradeLevel(User user){
            if(user.getId().equals(this.id))
                throw new TestUserServiceException();

            super.upgradeLevel(user);
        }
    }

    static class TestUserServiceException extends RuntimeException{

    }

}
