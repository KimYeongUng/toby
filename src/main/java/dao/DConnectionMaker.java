package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DConnectionMaker implements ConnectionMaker {
    @Override
    public Connection makeConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        Connection c = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test;AUTO_SERVER=true","sa","1234");
        return c;
    }
}
