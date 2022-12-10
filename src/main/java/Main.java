import dao.ConnectionMaker;
import dao.DConnectionMaker;
import dao.UserDao;
import domain.User;

import java.sql.SQLException;

// Client
public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        User user = new User();
        user.setId("1");
        user.setName("KIM");
        user.setPassword("1234");

        ConnectionMaker conn = new DConnectionMaker();
        UserDao dao = new UserDao(conn);
        int ret = dao.deleteAll();
        System.out.println("DELETE RET:"+ret);
        dao.add(user);

    }
}
