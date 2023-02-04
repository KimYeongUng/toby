package service;

import dao.UserDao;
import domain.Level;
import domain.User;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    public static final int MIN_LOGIN_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;
    UserDao dao;

    private DataSource dataSource;

    private PlatformTransactionManager transactionManager;

    public UserService() {
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public void setUserDao(UserDao dao){
        this.dao = dao;
    }

    public void upgradeLevels() throws SQLException {
         this.transactionManager
                = new DataSourceTransactionManager(dataSource);
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        try{
            List<User> users = dao.getAll();
            for (User user:users){
                if(canUpgradeLevel(user))
                    upgradeLevel(user);
            }
            transactionManager.commit(status);
        }catch (Exception e){
            transactionManager.rollback(status);
            throw e;
        }
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
}
