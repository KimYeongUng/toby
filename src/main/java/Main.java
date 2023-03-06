import dao.TobyConfigure;
import dao.UserDaoImpl;
import domain.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

// Client
public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        ApplicationContext context = new AnnotationConfigApplicationContext(TobyConfigure.class);

        User user = new User();
        user.setId("1");
        user.setName("KIM");
        user.setPassword("1234");

        UserDaoImpl dao = context.getBean("userDao", UserDaoImpl.class);
        int ret = dao.deleteAll();
        System.out.println("DELETE RET:"+ret);
        dao.add(user);

    }
}
