package service.impl;

import dao.UserDao;
import domain.Level;
import domain.User;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import service.UserService;

import javax.sql.DataSource;
import java.util.List;

public class UserServiceImpl implements UserService {
    public static final int MIN_LOGIN_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;
    UserDao dao;

    private DataSource dataSource;

    private PlatformTransactionManager transactionManager;

    private MailSender mailSender;

    public UserServiceImpl() {
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public void setUserDao(UserDao dao){
        this.dao = dao;
    }

    public void upgradeLevels(){
        List<User> users = dao.getAll();
        for (User user:users){
            if(canUpgradeLevel(user)) {
                upgradeLevel(user);
                sendUpgradeEmail(user);
            }
        }
//         this.transactionManager
//                = new DataSourceTransactionManager(dataSource);
//        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
//        try{
//            upgradeLevelIntenal();
//            transactionManager.commit(status);
//        }catch (Exception e){
//            transactionManager.rollback(status);
//            throw e;
//        }
    }

    protected void upgradeLevel(User user) {
        user.upgradeLevel();
        dao.update(user);
    }

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        return switch (currentLevel) {
            case BASIC -> (user.getLogin() >= MIN_LOGIN_FOR_SILVER);
            case SILVER -> (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
            case GOLD -> false;
        };
    }

    public void add(User user) {
        if(user.getLevel() == null)
            user.setLevel(Level.BASIC);

        dao.add(user);
    }

    private void sendUpgradeEmail(User user){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setFrom("useradmin@spring.io");
        message.setSubject("Upgrade Info.");
        message.setText("Your level is upgraded: "+user.getLevel());

        this.mailSender.send(message);
    }
}
