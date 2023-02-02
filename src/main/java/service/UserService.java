package service;

import dao.UserDao;
import domain.Level;
import domain.User;
import java.util.List;

public class UserService {
    public static final int MIN_LOGIN_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;
    UserDao dao;

    public void setUserDao(UserDao dao){
        this.dao = dao;
    }

    public void upgradeLevels(){
        List<User> users = dao.getAll();

        for (User user:users){
            if(canUpgradeLevel(user))
                upgradeLevel(user);
        }
    }

    private void upgradeLevel(User user) {
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
