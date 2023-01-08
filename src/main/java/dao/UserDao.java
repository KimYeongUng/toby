package dao;

import domain.User;
import java.util.List;

public interface UserDao {
     void add(User user);
     User get(String id);
     List<User> getAll();
     int deleteAll();
     int getCount();
}
